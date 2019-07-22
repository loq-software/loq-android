package com.loq.buggadooli.loq2.ui.fragments

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.loq.buggadooli.loq2.R
import com.loq.buggadooli.loq2.constants.Constants
import com.loq.buggadooli.loq2.extensions.inflateTo
import com.loq.buggadooli.loq2.extensions.safeActivity
import com.loq.buggadooli.loq2.ui.viewmodels.SetAndForgetViewModel
import kotlinx.android.synthetic.main.fragment_set_or_forget.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class SetAndForgetFragment: Fragment(), TimePickerDialog.OnTimeSetListener {

    private lateinit var applicationName: String
    private val viewModel by sharedViewModel<SetAndForgetViewModel>()

    private var rawStartMinute = ""
    private var rawStartHour = ""
    private var rawEndMinute = ""
    private var rawEndHour = ""

    private var isStartTime = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applicationName = arguments?.getString(Constants.APP_NAME, "")?: ""
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflateTo(R.layout.fragment_set_or_forget, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDays()
        btnStartTime!!.setOnClickListener {
            isStartTime = true
            showTimePicker()
        }
        btnEndTime!!.setOnClickListener {
            isStartTime = false
            showTimePicker()
        }
        btnFinished.setOnClickListener {
            val startTime = btnStartTime.text.toString()
            val endTime = btnEndTime.text.toString()
            if (startTime.isNotBlank() && endTime.isNotBlank()) {
                viewModel.finishButtonClicked(
                        applicationName,
                        btnStartTime!!.text.toString(),
                        btnEndTime!!.text.toString(),
                        rawStartMinute,
                        rawEndMinute,
                        rawStartHour,
                        rawEndHour,
                        this)
            }
            else{
                Toast.makeText(safeActivity, "You must enter a start and end time", Toast.LENGTH_LONG).show()
            }
        }
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
        if (isStartTime) {
            rawStartHour = rawHour.toString()
            rawStartMinute = minute.toString()
            btnStartTime!!.text = "$hourStr:$minuteStr $timeOfDay"
        } else {
            rawEndHour = rawHour.toString()
            rawEndMinute = minute.toString()
            btnEndTime!!.text = "$hourStr:$minuteStr $timeOfDay"
        }    }

}