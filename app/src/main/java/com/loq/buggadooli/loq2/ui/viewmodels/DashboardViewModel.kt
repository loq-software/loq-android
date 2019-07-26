package com.loq.buggadooli.loq2.ui.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.loq.buggadooli.loq2.extensions.disposeOnDetach
import com.loq.buggadooli.loq2.extensions.ioToMain
import com.loq.buggadooli.loq2.extensions.subscribeForOutcome
import com.loq.buggadooli.loq2.models.BlockedApplication
import com.loq.buggadooli.loq2.network.Outcome
import com.loq.buggadooli.loq2.network.api.AuthenticationService
import com.loq.buggadooli.loq2.network.api.LoqService
import com.loq.buggadooli.loq2.utils.Event

class DashboardViewModel(private val service: LoqService, private val authenticationService: AuthenticationService): ViewModel() {

    val loqsRecieved: LiveData<Event<List<BlockedApplication>>> get() = _loqsRecieved
    private val _loqsRecieved = MutableLiveData<Event<List<BlockedApplication>>>()

    val showToast: LiveData<Event<String>> get() = _showToast
    private val _showToast = MutableLiveData<Event<String>>()

    fun getLoqs(view: View){
        val userId = authenticationService.getCurrentUser()?.uid?: ""
        service.getLoqs(userId)
                .ioToMain()
                .subscribeForOutcome { outcome ->
                   when(outcome){
                       is Outcome.Success ->{
                           val loqs = outcome.data
                           _loqsRecieved.postValue(Event(loqs))
                       }
                       is Outcome.ApiError -> {
                           outcome.e.printStackTrace()
                           _showToast.postValue(Event("Network error"))
                       }
                       is Outcome.Failure ->{
                           outcome.e.printStackTrace()
                           _showToast.postValue(Event("Error loading data"))
                       }
                   }
                }
                .disposeOnDetach(view)
    }
}