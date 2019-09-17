package com.easystreetinteractive.loq.ui.viewmodels

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.widget.CheckBox
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.easystreetinteractive.loq.extensions.*
import com.easystreetinteractive.loq.models.BlockedApplication
import com.easystreetinteractive.loq.models.BlockedDay
import com.easystreetinteractive.loq.network.api.AuthenticationService
import com.easystreetinteractive.loq.utils.Event
import java.util.ArrayList

class SetAndForgetViewModel(
        private val manager: PackageManager,
        private val authentication: AuthenticationService
):ViewModel(){

    var days: MutableList<CheckBox> = ArrayList()

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

    fun buildLoqsList(
            loqToEdit: BlockedApplication?,
            info: List<ApplicationInfo>,
            currentLoqs: List<BlockedApplication>,
            days: MutableCollection<BlockedDay>
    ): List<BlockedApplication>{
        val applications = ArrayList<BlockedApplication>()
        val user = authentication.getCurrentUser()?: return applications

        var found: Boolean


        if (loqToEdit == null) {

            for (application in info) {
                found = false
                for (loq in currentLoqs) {
                    found = application.getAppName(manager).contentEquals(loq.applicationName)
                    if (found) {
                        loq.blockBlockedDays.addAll(days)
                        applications.add(loq)
                        break
                    }
                }
                if (!found) {
                    val blockedApplication = BlockedApplication("", user.uid, application.getAppName(manager), application.packageName, days.toMutableList())
                    applications.add(blockedApplication)
                }

            }
        }
        else{
            val currentDays = loqToEdit.blockBlockedDays
            for (selectedDay in days){
                currentDays.add(selectedDay)
            }
            applications.add(loqToEdit)
        }
        return applications
    }
}