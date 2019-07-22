package com.loq.buggadooli.loq2.ui.viewmodels

import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.loq.buggadooli.loq2.extensions.attachLifecycle
import com.loq.buggadooli.loq2.extensions.hasSelection
import com.loq.buggadooli.loq2.extensions.ioToMain
import com.loq.buggadooli.loq2.extensions.subscribeForOutcome
import com.loq.buggadooli.loq2.models.Loq
import com.loq.buggadooli.loq2.network.Outcome
import com.loq.buggadooli.loq2.repositories.ApplicationsRepository
import com.loq.buggadooli.loq2.utils.Event
import java.util.ArrayList

class SetAndForgetViewModel(private val repository: ApplicationsRepository):ViewModel(){

    var days: MutableList<CheckBox> = ArrayList()

    val onLockedApplicationSaved: LiveData<Event<Loq>> get() = _onLockedApplicationSaved
    private val _onLockedApplicationSaved = MutableLiveData<Event<Loq>>()

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

        repository.saveApplication(
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
                .attachLifecycle(owner)
    }
}