package com.easystreetinteractive.loq.ui.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.easystreetinteractive.loq.extensions.attachLifecycle
import com.easystreetinteractive.loq.extensions.ioToMain
import com.easystreetinteractive.loq.extensions.subscribeForOutcome
import com.easystreetinteractive.loq.models.BlockedApplication
import com.easystreetinteractive.loq.network.Outcome
import com.easystreetinteractive.loq.network.api.AuthenticationService
import com.easystreetinteractive.loq.network.api.LoqService
import com.easystreetinteractive.loq.utils.Event
import java.lang.NullPointerException

class EditLoqViewModel(
        private val loqService: LoqService,
        private val authenticationService: AuthenticationService
): ViewModel() {

    val loqUpdated: LiveData<Event<BlockedApplication>> get() = _loqUpdated
    private val _loqUpdated = MutableLiveData<Event<BlockedApplication>>()

    fun updateLoq(owner: LifecycleOwner, loq: BlockedApplication) {
        val id = authenticationService.getCurrentUser()?.uid?: throw NullPointerException("user is null")
        loqService.addLoq(id, loq)
                .ioToMain()
                .subscribeForOutcome { outcome ->
                    when(outcome){

                        is Outcome.Success -> {
                            _loqUpdated.postValue(Event(outcome.data))
                        }
                    }
                }
                .attachLifecycle(owner)
    }

    fun removeLoq(owner: LifecycleOwner, loq: BlockedApplication) {
        val userId = authenticationService.getCurrentUser()?.uid?: throw NullPointerException("user is null")

        loqService.removeLoq(userId, loq)
                .ioToMain()
                .subscribeForOutcome { outcome ->
                    when(outcome){
                        is Outcome.Success -> {
                            _loqUpdated.postValue(Event(outcome.data))
                        }
                    }
                }
                .attachLifecycle(owner)
    }

}