package com.loq.buggadooli.loq2.ui.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.loq.buggadooli.loq2.extensions.attachLifecycle
import com.loq.buggadooli.loq2.extensions.ioToMain
import com.loq.buggadooli.loq2.extensions.subscribeForOutcome
import com.loq.buggadooli.loq2.network.Outcome
import com.loq.buggadooli.loq2.repositories.ApplicationsRepository
import com.loq.buggadooli.loq2.utils.Event

class EasyLoqViewModel(private val repository: ApplicationsRepository): ViewModel() {

    val onUserHasApplication: LiveData<Event<Boolean>> get() = __onUserHasApplication
    private val __onUserHasApplication = MutableLiveData<Event<Boolean>>()

    fun checkIfUserHasAppOnDevice(appName: String, lifecycleOwner: LifecycleOwner){
        repository.getHasApplicationInstalled(appName)
                .ioToMain()
                .subscribeForOutcome { outcome ->
                    when(outcome){
                        is Outcome.Success ->{
                            val success = outcome.data
                            __onUserHasApplication.postValue(Event(success))
                        }
                    }
                }
                .attachLifecycle(lifecycleOwner)
    }

}