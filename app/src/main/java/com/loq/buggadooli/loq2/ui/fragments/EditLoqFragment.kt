package com.loq.buggadooli.loq2.ui.fragments

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TimePicker
import androidx.fragment.app.Fragment

import com.loq.buggadooli.loq2.models.Loq
import com.loq.buggadooli.loq2.R
import com.loq.buggadooli.loq2.constants.Constants
import com.loq.buggadooli.loq2.extensions.inflateTo
import com.loq.buggadooli.loq2.extensions.safeActivity
import com.loq.buggadooli.loq2.utils.Utils
import kotlinx.android.synthetic.main.fragment_edit_loq.*

import java.util.ArrayList
import java.util.Calendar

class EditLoqFragment : Fragment(), TimePickerDialog.OnTimeSetListener {

    private val days = ArrayList<CheckBox>()
    private var thisLoq: Loq? = null
    private var loqIndex: Int = 0
    private var isStartTime = false
    private var rawStartHour: String? = null
    private var rawStartMinute: String? = null
    private var rawEndHour: String? = null
    private var rawEndMinute: String? = null

    private val selectedDays: List<String>
        get() {
            val selectedDays = ArrayList<String>()
            for (item in days) {
                if (item.isChecked)
                    selectedDays.add(item.text.toString())
            }
            return selectedDays
        }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflateTo(R.layout.fragment_edit_loq, container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loqIndex = arguments?.getInt(Constants.LOQ_INDEX, 0)?: 0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDayCheckboxes()
        btnStartTime.setOnClickListener {
            isStartTime = true
            showTimePicker()
        }
        btnEndTime.setOnClickListener {
            isStartTime = false
            showTimePicker()
        }
        initLoq()
        btnSaveChanges.setOnClickListener {
            saveEditedLoqs()
            safeActivity.onBackPressed()
        }
    }

    private fun saveEditedLoqs() {
        thisLoq?.startTime = btnStartTime!!.text.toString()
        thisLoq?.rawStartHour = rawStartHour
        thisLoq?.rawStartMinute = rawStartMinute
        thisLoq?.rawEndHour = rawEndHour
        thisLoq?.rawEndMinute = rawEndMinute
        thisLoq?.endTime = btnEndTime!!.text.toString()
        val selectedDays = selectedDays
        if (!selectedDays.isEmpty()) {
            thisLoq?.days = selectedDays
            var days = ""
            val thisLoqDays = thisLoq?.days?: return
            for (day in thisLoqDays) {
                days += "$day "
            }
            thisLoq?.daysStr = days
        }
       /* val loqs = Utils.INSTANCE.currentLoqs
        loqs.removeAt(loqIndex)
        loqs.add(loqIndex, thisLoq)
        Utils.INSTANCE.currentLoqs = loqs
        val json = Utils.INSTANCE.convertLoqsToJson(loqs)
        Utils.INSTANCE.saveLoqsToFile(safeActivity, json)*/ //todo: 7/25/19 Handle this
    }

    private fun initLoq() {
       // thisLoq = Utils.INSTANCE.editLoq //todo: 7/25/19 Handle this
        var day: String
        val thisLoqDays = thisLoq?.days
        if (thisLoqDays != null) {
            for (chkDay in days) {
                day = chkDay.text.toString()
                if (thisLoq!!.days!!.contains(day)) {
                    chkDay.isChecked = true
                }
            }
        }

        btnStartTime!!.text = thisLoq!!.startTime
        btnEndTime!!.text = thisLoq!!.endTime
        txtAppName!!.text = thisLoq!!.appName
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

    private fun getDayCheckboxes() {
        days.add(chkSunday)
        days.add(chkMonday)
        days.add(chkTuesday)
        days.add(chkWednesday)
        days.add(chkThursday)
        days.add(chkFriday)
        days.add(chkSaturday)
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        var hourOfDay = hourOfDay
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
        }
    }
}
