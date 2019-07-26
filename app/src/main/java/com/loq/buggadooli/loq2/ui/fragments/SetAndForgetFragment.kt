package com.loq.buggadooli.loq2.ui.fragments

import android.app.TimePickerDialog
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.loq.buggadooli.loq2.R
import com.loq.buggadooli.loq2.constants.Constants
import com.loq.buggadooli.loq2.extensions.*
import com.loq.buggadooli.loq2.models.BlockTime
import com.loq.buggadooli.loq2.models.BlockedDay
import com.loq.buggadooli.loq2.ui.viewmodels.SetAndForgetViewModel
import com.loq.buggadooli.loq2.utils.BlockedDayUtilities.buildDay
import kotlinx.android.synthetic.main.fragment_set_or_forget.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*
import kotlin.collections.HashMap

class SetAndForgetFragment: Fragment(), TimePickerDialog.OnTimeSetListener {

    private var isStartTimeForDifferent: Boolean = false
    private var differentDaysEndMinute: Int = -1
    private var differentDaysEndHour: Int = -1
    private var differentDaysStartMinute: Int = -1
    private var differentDaysStartHour: Int = -1
    private var currentDayForDifferentDaysSetting: BlockedDay? = null
    private val selectedDaysForDifferentDaysSetting = HashMap<String,BlockedDay>()
    private var selectedApplications: List<ApplicationInfo> = emptyList()
    private lateinit var applicationName: String
    private val viewModel by sharedViewModel<SetAndForgetViewModel>()

    private var rawStartMinute = ""
    private var rawStartHour = ""
    private var rawEndMinute = ""
    private var rawEndHour = ""

    private var isStartTime = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedApplications = arguments?.getParcelableArrayList(Constants.APP_NAME)?: emptyList()
        applicationName = selectedApplications.firstOrNull()?.loadLabel(safeActivity.packageManager)?.toString()?: ""
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflateTo(R.layout.fragment_set_or_forget, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDays()

        val adapter = ArrayAdapter.createFromResource(safeActivity, R.array.days, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        daysSpinner.adapter = adapter
        daysSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                differentDaysStartHour = -1
                differentDaysStartMinute = -1
                differentDaysEndHour = -1
                differentDaysEndMinute = -1

                val item = adapter.getItem(position)?: ""
                if (! item.toString().contentEquals("Select Day")) {
                    currentDayForDifferentDaysSetting = buildDay(item.toString())
                }
                else{
                    currentDayForDifferentDaysSetting = null
                }
            }

        }

        btnStartTime!!.setOnClickListener {
            isStartTime = true
            showTimePicker()
        }
        btnEndTime!!.setOnClickListener {
            isStartTime = false
            showTimePicker()
        }

        btnStartTimeDifferent.setOnClickListener {
            isStartTimeForDifferent = true
            showTimePicker()
        }
        btnEndTimeDifferent.setOnClickListener {
            isStartTimeForDifferent = false
            showTimePicker()
        }

        addDayButton.setOnClickListener {
            val day = currentDayForDifferentDaysSetting

            if (day == null){
                Toast.makeText(safeActivity, "You must select a day", Toast.LENGTH_SHORT).show()
            }
            else{
                if (differentDaysEndHour <0 || differentDaysEndMinute < 0 || differentDaysStartHour < 0 || differentDaysStartMinute < 0){
                    Toast.makeText(safeActivity, "You must enter a start/end time", Toast.LENGTH_SHORT).show()
                }
                else{
                    day.time = BlockTime(differentDaysStartHour, differentDaysStartMinute, differentDaysEndHour, differentDaysEndMinute)
                    selectedDaysForDifferentDaysSetting[day.dayOfWeek] = day
                    Toast.makeText(safeActivity, "Lock for ${day.dayOfWeek} added", Toast.LENGTH_SHORT).show()
                }
            }
        }
        btnFinished.setOnClickListener {
            when(radioButtonGroup.checkedRadioButtonId){
                R.id.differentDaysRadioButton ->{
                    if (selectedDaysForDifferentDaysSetting.isEmpty()){
                        Toast.makeText(safeActivity, "You must add a day", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        viewModel.finishButtonClicked(selectedApplications, selectedDaysForDifferentDaysSetting.values, view)
                    }
                }

                R.id.sameTimesRadioButton -> {
                    val startTime = btnStartTime.text.toString()
                    val endTime = btnEndTime.text.toString()
                    if (viewModel.days.hasSelection()) {
                        if (startTime.isNotBlank() && endTime.isNotBlank()) {
                            val application = selectedApplications.first()
                            viewModel.finishButtonClickedOld(
                                    application,
                                    btnStartTime!!.text.toString(),
                                    btnEndTime!!.text.toString(),
                                    rawStartMinute,
                                    rawEndMinute,
                                    rawStartHour,
                                    rawEndHour,
                                    view)
                        } else {
                            Toast.makeText(safeActivity, "You must enter a start and end time", Toast.LENGTH_LONG).show()

                        }
                    } else {
                        Toast.makeText(safeActivity, "You must select a day(s)", Toast.LENGTH_LONG).show()

                    }
                }
                else -> {
                    Toast.makeText(safeActivity, "Please select an option", Toast.LENGTH_LONG).show()

                }
            }
        }

        viewModel.onLockedApplicationSaved.observe(this, Observer { event ->
            val application = event.getContentIfNotHandled()
            if (application != null){
                safeActivity.toast("Application(s) saved")
                safeActivity.popAllInBackStack()
                safeActivity.replaceFragment(fragment = DashboardFragment())
            }
        })

        viewModel.onLockedApplicationsSaved.observe(this, Observer { event ->
            val applications = event.getContentIfNotHandled()
            applications?.let {
                safeActivity.toast("Applications saved")
                safeActivity.popAllInBackStack()
                safeActivity.replaceFragment(fragment = DashboardFragment())
            }
        })
    }

    private fun setupDays() {
        viewModel.setupDays(
                chkSunday,
                chkMonday,
                chkTuesday,
                chkWednesday,
                chkThursday,
                chkFriday,
                chkSaturday
        )
    }

    private fun showTimePicker() {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        val dialog = TimePickerDialog(safeActivity, this, hour, minute,
                DateFormat.is24HourFormat(safeActivity))
        dialog.show()
    }

    override fun onTimeSet(view: TimePicker, hour: Int, minute: Int) {
        var hourOfDay = hour
        val rawHour = hourOfDay
        var timeOfDay = "AM"
        if (hourOfDay > 11) {
            timeOfDay = "PM"
        }

        if (hourOfDay > 12) {
            hourOfDay -= 12
        }

        val hourStr = hourOfDay.toString()
        val minuteStr = minute.toString()

        if (radioButtonGroup.checkedRadioButtonId == R.id.sameTimesRadioButton){
            if (isStartTime) {
                rawStartHour = rawHour.toString()
                rawStartMinute = minute.toString()
                btnStartTime!!.text = "$hourStr:$minuteStr $timeOfDay"
            } else {
                rawEndHour = rawHour.toString()
                rawEndMinute = minute.toString()
                btnEndTime!!.text = "$hourStr:$minuteStr $timeOfDay"
            }
        }
        else{
            if (isStartTimeForDifferent){
                differentDaysStartHour = hour
                differentDaysStartMinute = minute
                btnStartTimeDifferent!!.text = "$hourStr:$minuteStr $timeOfDay"

            }
            else{
                differentDaysEndHour = hour
                differentDaysEndMinute = minute
                btnEndTimeDifferent!!.text = "$hourStr:$minuteStr $timeOfDay"
            }
        }

    }

}
