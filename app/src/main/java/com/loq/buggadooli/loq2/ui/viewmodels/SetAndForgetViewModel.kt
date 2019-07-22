package com.loq.buggadooli.loq2.ui.viewmodels

import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.loq.buggadooli.loq2.extensions.attachLifecycle
import com.loq.buggadooli.loq2.extensions.ioToMain
import com.loq.buggadooli.loq2.extensions.subscribeForOutcome
import com.loq.buggadooli.loq2.network.Outcome
import com.loq.buggadooli.loq2.repositories.ApplicationsRepository
import java.util.ArrayList

class SetAndForgetViewModel(private val repository: ApplicationsRepository):ViewModel(){

    private var days: MutableList<CheckBox> = ArrayList()

    fun setupDays(
            sunday: CheckBox,
            monday: CheckBox,
            tuesday: CheckBox,
            wednesday: CheckBox,
            thursday: CheckBox,
            friday: CheckBox,
            saturday: CheckBox
    ){
        days.clear()
        days.add(sunday)
        days.add(monday)
        days.add(tuesday)
        days.add(wednesday)
        days.add(thursday)
        days.add(friday)
        days.add(saturday)
    }

    fun finishButtonClicked(applicationName: String,
                            startTime: String,
                            endTime: String,
                            rawStartMinute: String,
                            rawEndMinute: String,
                            rawStartHour:String,
                            rawEndHour: String,
                            owner: Fragment) {

        repository.saveApplication(applicationName, days, startTime, endTime, rawStartMinute, rawEndMinute, rawStartHour, rawEndHour)
                .ioToMain()
                .subscribeForOutcome { outcome ->
                    when(outcome){
                        is Outcome.Success ->{

                        }
                    }
                }
                .attachLifecycle(owner)
    }

    /*private fun convertLoqsToJson(): String {
        val jsonObj = JSONObject()
        val jsonArr = JSONArray()
        for ((AppName, _, daysStr, startTime, endTime, packageName, rawStartHour1, rawStartMinute1, rawEndHour1, rawEndMinute1) in newLoqs) {
            val obj = JSONObject()
            try {
                obj.put("AppName", AppName)
                obj.put("days", daysStr)
                obj.put("PackageName", packageName)
                obj.put("StartTime", startTime)
                obj.put("EndTime", endTime)
                obj.put("EndHour", rawEndHour1)
                obj.put("EndMinute", rawEndMinute1)
                obj.put("StartHour", rawStartHour1)
                obj.put("StartMinute", rawStartMinute1)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            jsonArr.put(obj)
        }

        try {
            jsonObj.put("Loqs", jsonArr)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonObj.toString()
    }*/
}