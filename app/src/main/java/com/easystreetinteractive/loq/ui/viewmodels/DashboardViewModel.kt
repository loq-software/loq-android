package com.easystreetinteractive.loq.ui.viewmodels

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
import com.easystreetinteractive.loq.utils.Event

class DashboardViewModel(
        private val authenticationService: AuthenticationService,
        private val pinManager: PinManager
): ViewModel() {

    val onLogout: LiveData<Event<Boolean>> get() = _onLogOut
    private val _onLogOut = MutableLiveData<Event<Boolean>>()

    val showToast: LiveData<Event<String>> get() = _showToast
    private val _showToast = MutableLiveData<Event<String>>()

    fun checkForPin(activity: FragmentActivity) {
        if (pinManager.hasStoredPin()){
            PinDialog.show(activity)
        }
    }

    fun logout(activity: FragmentActivity) {
        authenticationService.logout()
        _onLogOut.postValue(Event(true))
    }

}