package com.easystreetinteractive.loq.ui.viewmodels

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.easystreetinteractive.loq.extensions.attachLifecycle
import com.google.firebase.auth.FirebaseUser
import com.easystreetinteractive.loq.extensions.disposeOnDetach
import com.easystreetinteractive.loq.extensions.ioToMain
import com.easystreetinteractive.loq.extensions.subscribeForOutcome
import com.easystreetinteractive.loq.models.BlockedApplication
import com.easystreetinteractive.loq.network.Outcome
import com.easystreetinteractive.loq.network.api.AuthenticationService
import com.easystreetinteractive.loq.network.api.LoqService
import com.easystreetinteractive.loq.utils.Event

class MainViewModel(private val authenticationService: AuthenticationService, private val loqService: LoqService): ViewModel() {

    val onLoqsLoaded: LiveData<Event<List<BlockedApplication>>> get() = _onLoqsLoaded
    private val _onLoqsLoaded = MutableLiveData<Event<List<BlockedApplication>>>()

    val showError: LiveData<Event<String>> get() = _showError
    private val _showError = MutableLiveData<Event<String>>()

    fun loadLoqs(owner: LifecycleOwner) {
        val id = authenticationService.getCurrentUser()?.uid?: return
        loqService.getLoqs(id)
                .ioToMain()
                .subscribeForOutcome { outcome ->
                    when(outcome){
                        is Outcome.Success ->{
                            _onLoqsLoaded.postValue(Event(outcome.data))
                        }
                        is Outcome.ApiError ->{
                            _showError.postValue(Event("Network error"))
                        }
                        is Outcome.Failure ->{
                            _showError.postValue(Event("Loading error"))
                        }
                    }
                }
                .attachLifecycle(owner)
    }


    val user: FirebaseUser? get() = authenticationService.getCurrentUser()

}