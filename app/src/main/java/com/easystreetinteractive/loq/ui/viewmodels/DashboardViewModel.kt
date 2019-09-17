package com.easystreetinteractive.loq.ui.viewmodels

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
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
import com.easystreetinteractive.loq.pin.PinManager
import com.easystreetinteractive.loq.ui.dialogs.PinDialog
import com.easystreetinteractive.loq.ui.fragments.DashboardFragment
import com.easystreetinteractive.loq.utils.Event

class DashboardViewModel(
        private val service: LoqService,
        private val authenticationService: AuthenticationService,
        private val pinManager: PinManager
): ViewModel() {

    val loqsRecieved: LiveData<Event<List<BlockedApplication>>> get() = _loqsRecieved
    private val _loqsRecieved = MutableLiveData<Event<List<BlockedApplication>>>()

    val showToast: LiveData<Event<String>> get() = _showToast
    private val _showToast = MutableLiveData<Event<String>>()

    fun getLoqs(owner: LifecycleOwner){
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
                .attachLifecycle(owner)
    }

    fun checkForPin(activity: FragmentActivity) {
        if (pinManager.hasStoredPin()){
            PinDialog.show(activity)
        }
    }

}