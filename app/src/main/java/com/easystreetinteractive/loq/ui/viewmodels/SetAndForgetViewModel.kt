package com.easystreetinteractive.loq.ui.viewmodels

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.view.View
import android.widget.CheckBox
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.easystreetinteractive.loq.extensions.*
import com.easystreetinteractive.loq.models.BlockTime
import com.easystreetinteractive.loq.models.BlockedApplication
import com.easystreetinteractive.loq.models.BlockedDay
import com.easystreetinteractive.loq.network.api.LoqService
import com.easystreetinteractive.loq.network.Outcome
import com.easystreetinteractive.loq.network.api.AuthenticationService
import com.easystreetinteractive.loq.utils.Event
import java.util.ArrayList

class SetAndForgetViewModel(
        private val loqService: LoqService,
        private val manager: PackageManager,
        private val authentication: AuthenticationService
):ViewModel(){

    var days: MutableList<CheckBox> = ArrayList()

    val onLockedApplicationsSaved: LiveData<Event<List<BlockedApplication>>> get() = _onLockedApplicationsSaved
    private val _onLockedApplicationsSaved = MutableLiveData<Event<List<BlockedApplication>>>()

    private val _errorAddingLoq = MutableLiveData<Event<Throwable>>()

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

    fun finishButtonClickedOld(
            loqToEdit: BlockedApplication?,
            info: List<ApplicationInfo>,
            currentLoqs: List<BlockedApplication>,
            rawStartMinute: String,
            rawEndMinute: String,
            rawStartHour:String,
            rawEndHour: String,
            view: View) {

        val user = authentication.getCurrentUser()?: return

        val applications = ArrayList<BlockedApplication>()
        val blockedDays = days.dayCheckBoxesToBlockedDays(BlockTime(rawStartHour.toInt(), rawStartMinute.toInt(), rawEndHour.toInt(), rawEndMinute.toInt()))

        var found: Boolean

        if (loqToEdit == null){
            for (application in info){
                found = false

                for(loq in currentLoqs){
                    found = application.getAppName(view.context.packageManager).contentEquals(loq.applicationName)
                    if (found){
                        loq.blockBlockedDays.clear()
                        loq.blockBlockedDays.addAll(blockedDays)
                        applications.add(loq)
                    }
                }

                if (! found){
                    val newLoq = BlockedApplication("", user.uid, application.getAppName(manager), application.packageName, blockedDays)
                    applications.add(newLoq)
                }

            }
        }
        else{
            val days = loqToEdit.blockBlockedDays
            for(selectedDay in blockedDays){
                found = false
                for (day in days){
                    found = day.dayOfWeek.contentEquals(selectedDay.dayOfWeek)
                    if (found){
                        day.time = selectedDay.time
                    }
                }
                if (!found){
                    days.add(selectedDay)
                }
            }
            applications.add(loqToEdit)
        }


        loqService.addLoqs(user.uid, applications)
                .ioToMain()
                .subscribeForOutcome { outcome ->
                    when(outcome){
                        is Outcome.Success ->{
                            _onLockedApplicationsSaved.postValue(Event(outcome.data))
                        }
                        is Outcome.ApiError ->{
                            _errorAddingLoq.postValue(Event(outcome.e))
                        }
                    }
                }
                .disposeOnDetach(view)
    }

    fun finishButtonClicked(
            loqToEdit: BlockedApplication?,
            info: List<ApplicationInfo>,
            currentLoqs: List<BlockedApplication>,
            days: MutableCollection<BlockedDay>,
            view: View
    ){

        var found: Boolean

        val user = authentication.getCurrentUser()?: return
        val applications = ArrayList<BlockedApplication>()

        if (loqToEdit == null) {

            for (infoItem in info) {
                found = false
                for (loq in currentLoqs) {
                    found = infoItem.getAppName(view.context.packageManager).contentEquals(loq.applicationName)
                    if (found) {
                        loq.blockBlockedDays.clear()
                        loq.blockBlockedDays.addAll(days)
                        applications.add(loq)
                    }
                }
                if (!found) {
                    val blockedApplication = BlockedApplication("", user.uid, infoItem.getAppName(manager), infoItem.packageName, days.toMutableList())
                    applications.add(blockedApplication)
                }

            }
        }
        else{
            val currentDays = loqToEdit.blockBlockedDays
            for(selectedDay in currentDays){
                found = false
                for (day in days){
                    found = day.dayOfWeek.contentEquals(selectedDay.dayOfWeek)
                    if (found){
                        day.time = selectedDay.time
                    }
                }
                if (!found){
                    days.add(selectedDay)
                }
            }
            applications.add(loqToEdit)
        }
        loqService.addLoqs(user.uid, applications)
                .ioToMain()
                .subscribeForOutcome { outcome ->
                    when(outcome){
                        is Outcome.Success ->{
                            _onLockedApplicationsSaved.postValue(Event(outcome.data))
                        }
                        is Outcome.ApiError ->{
                            _errorAddingLoq.postValue(Event(outcome.e))
                        }
                    }
                }
                .disposeOnDetach(view)
    }
}