package com.loq.buggadooli.loq2.ui.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.loq.buggadooli.loq2.extensions.disposeOnDetach
import com.loq.buggadooli.loq2.extensions.ioToMain
import com.loq.buggadooli.loq2.extensions.subscribeForOutcome
import com.loq.buggadooli.loq2.models.BlockedApplication
import com.loq.buggadooli.loq2.network.Outcome
import com.loq.buggadooli.loq2.network.api.AuthenticationService
import com.loq.buggadooli.loq2.network.api.LoqService
import com.loq.buggadooli.loq2.utils.Event

class MainViewModel(private val authenticationService: AuthenticationService, private val loqService: LoqService): ViewModel() {

    val onLoqsLoaded: LiveData<Event<List<BlockedApplication>>> get() = _onLoqsLoaded
    private val _onLoqsLoaded = MutableLiveData<Event<List<BlockedApplication>>>()

    val showError: LiveData<Event<String>> get() = _showError
    private val _showError = MutableLiveData<Event<String>>()

    fun loadLoqs(view: View) {
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
                .disposeOnDetach(view)
    }


    val user: FirebaseUser? get() = authenticationService.getCurrentUser()

}