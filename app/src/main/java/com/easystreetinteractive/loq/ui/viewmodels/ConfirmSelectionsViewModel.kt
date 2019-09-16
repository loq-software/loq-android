package com.easystreetinteractive.loq.ui.viewmodels

import androidx.fragment.app.FragmentActivity
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
import com.easystreetinteractive.loq.ui.dialogs.ErrorDialog
import com.easystreetinteractive.loq.utils.Event
import java.lang.NullPointerException

class ConfirmSelectionsViewModel(
        private val loqService: LoqService,
        private val authenticationService: AuthenticationService,
        private val pinManager: PinManager
): ViewModel() {

    val onLockedApplicationsSaved: LiveData<Event<List<BlockedApplication>>> get() = _onLockedApplicationsSaved
    private val _onLockedApplicationsSaved = MutableLiveData<Event<List<BlockedApplication>>>()

    private fun addLoqs(activity: FragmentActivity, userUuid: String, applications: List<BlockedApplication>){
        loqService.addLoqs(userUuid, applications)
                .ioToMain()
                .subscribeForOutcome { outcome ->
                    when(outcome){
                        is Outcome.Success ->{
                            _onLockedApplicationsSaved.postValue(Event(outcome.data))
                        }
                        is Outcome.Failure ->{
                            ErrorDialog.show(activity, Throwable("Error adding loqs"))
                        }
                    }
                }
                .attachLifecycle(activity)
    }

    fun finishButtonClicked(
            activity: FragmentActivity,
            loqs: List<BlockedApplication>,
            isConfirmChecked: Boolean,
            isPinChecked: Boolean,
            pin: String?,
            confirmPin: String?
    ) {
        val user = authenticationService.getCurrentUser() ?: throw NullPointerException("User is null")

        if (isConfirmChecked) {

            if (isPinChecked) {
                if (pin != null && pin.isNotBlank()) {
                    if (confirmPin != null && confirmPin.isNotBlank()){
                        if (confirmPin != pin){
                            ErrorDialog.show(activity, Throwable("Pin numbers do not match"))
                            return
                        }
                        else{
                            pinManager.storePin(pin)
                        }
                    }
                    else{
                        ErrorDialog.show(activity, Throwable("You must confirm you pin number"))
                        return
                    }
                } else {
                    ErrorDialog.show(activity, Throwable("You must enter a valid pin number"))
                    return
                }

            } else {
                pinManager.removePin()
            }

            addLoqs(activity, user.uid, loqs)

        }
        else{
            ErrorDialog.show(activity, Throwable("You must select the checkbox to confirm agreement"))
        }

    }
}