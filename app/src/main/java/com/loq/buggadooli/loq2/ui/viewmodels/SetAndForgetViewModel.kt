package com.loq.buggadooli.loq2.ui.viewmodels

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.view.View
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.loq.buggadooli.loq2.extensions.*
import com.loq.buggadooli.loq2.models.BlockTime
import com.loq.buggadooli.loq2.models.BlockedApplication
import com.loq.buggadooli.loq2.models.BlockedDay
import com.loq.buggadooli.loq2.models.Loq
import com.loq.buggadooli.loq2.network.api.LoqService
import com.loq.buggadooli.loq2.network.Outcome
import com.loq.buggadooli.loq2.network.api.AuthenticationService
import com.loq.buggadooli.loq2.repositories.ApplicationsRepository
import com.loq.buggadooli.loq2.utils.Event
import java.util.ArrayList

class SetAndForgetViewModel(
        private val repository: ApplicationsRepository,
        private val loqService: LoqService,
        private val manager: PackageManager,
        private val authentication: AuthenticationService
):ViewModel(){

    var days: MutableList<CheckBox> = ArrayList()

    val onLockedApplicationSaved: LiveData<Event<BlockedApplication>> get() = _onLockedApplicationSaved
    private val _onLockedApplicationSaved = MutableLiveData<Event<BlockedApplication>>()

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

    fun finishButtonClickedOld(application: ApplicationInfo,
                               startTime: String,
                               endTime: String,
                               rawStartMinute: String,
                               rawEndMinute: String,
                               rawStartHour:String,
                               rawEndHour: String,
                               view: View) {

     /*   repository.saveApplication(
                applicationName,
                days,
                startTime,
                endTime,
                rawStartMinute,
                rawEndMinute,
                rawStartHour,
                rawEndHour
        )
                .ioToMain()
                .subscribeForOutcome { outcome ->
                    when(outcome){
                        is Outcome.Success ->{
                            _onLockedApplicationSaved.postValue(Event(outcome.data))
                        }
                    }
                }
                .attachLifecycle(owner)*/
        val user = authentication.getCurrentUser()?: return
        val blockedDays = days.dayCheckBoxesToBlockedDays(BlockTime(rawStartHour.toInt(), rawStartMinute.toInt(), rawEndHour.toInt(), rawEndMinute.toInt()))
        val loq = BlockedApplication("", user.uid, application.getAppName(manager), application.packageName, blockedDays)
        loqService.addLoq(user.uid, loq)
                .ioToMain()
                .subscribeForOutcome { outcome ->
                    when(outcome){
                        is Outcome.Success ->{
                            _onLockedApplicationSaved.postValue(Event(outcome.data))
                        }
                        is Outcome.ApiError ->{
                            _errorAddingLoq.postValue(Event(outcome.e))
                        }
                    }
                }
                .disposeOnDetach(view)
    }

    fun finishButtonClicked(
            info: List<ApplicationInfo>,
            days: MutableCollection<BlockedDay>,
            view: View
    ){
        val user = authentication.getCurrentUser()?: return
        val applications = ArrayList<BlockedApplication>()
        for (infoItem in info){
            val blockedApplication = BlockedApplication("", user.uid, infoItem.getAppName(manager), infoItem.packageName, days.toList())
            applications.add(blockedApplication)
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