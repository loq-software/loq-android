package com.easystreetinteractive.loq.ui.viewmodels

import android.content.pm.ApplicationInfo
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.easystreetinteractive.loq.constants.TimeType
import com.easystreetinteractive.loq.extensions.attachLifecycle
import com.easystreetinteractive.loq.extensions.dayOfWeekString
import com.easystreetinteractive.loq.extensions.getAppName
import com.easystreetinteractive.loq.extensions.ioToMain
import com.easystreetinteractive.loq.models.BlockTime
import com.easystreetinteractive.loq.models.BlockedApplication
import com.easystreetinteractive.loq.models.BlockedDay
import com.easystreetinteractive.loq.network.api.AuthenticationService
import com.easystreetinteractive.loq.network.api.LoqService
import com.easystreetinteractive.loq.repositories.ApplicationsRepository
import com.easystreetinteractive.loq.ui.dialogs.ErrorDialog
import com.easystreetinteractive.loq.utils.Event
import java.lang.NullPointerException
import java.util.*
import kotlin.collections.ArrayList

class OneTimeLoqViewModel(
        private val applicationsRepository: ApplicationsRepository,
        private val loqService: LoqService,
        private val authentication: AuthenticationService
): ViewModel() {

    var blockedApplications: List<BlockedApplication> = emptyList()

    val selectedDate: LiveData<Event<Calendar>> get() = _selectedDate
    private val _selectedDate = MutableLiveData<Event<Calendar>>()

    val selectedStartTime: LiveData<Event<Calendar>> get() = _selectedStartTime
    private val _selectedStartTime = MutableLiveData<Event<Calendar>>()

    val selectedEndTime: LiveData<Event<Calendar>> get() = _selectedEndTime
    private val _selectedEndTime = MutableLiveData<Event<Calendar>>()

    val applications: LiveData<List<ApplicationInfo>> get() = _applications
    private val _applications = MutableLiveData<List<ApplicationInfo>>()

    val selectedApplication: LiveData<Event<ApplicationInfo>> get() = _selectedApplication
    private val _selectedApplication = MutableLiveData<Event<ApplicationInfo>>()

    val addedBlockedApplication: LiveData<Event<BlockedApplication>> get() = _addedBlockedApplication
    private val _addedBlockedApplication = MutableLiveData<Event<BlockedApplication>>()

    fun loadApplications(activity: FragmentActivity) {
        applicationsRepository.getInstalledApps()
                .ioToMain()
                .subscribe({ applications ->
                    _applications.postValue(applications)
                }, { error ->
                    ErrorDialog.show(activity, error)
                })
                .attachLifecycle(activity)
    }

    fun setDate(date: Calendar) {
        _selectedDate.postValue(Event(date))
    }

    fun setSelectedApplication(index: Int) {
        val application = _applications.value?.get(index)?: throw NullPointerException("Selected application is null")
        _selectedApplication.postValue(Event(application))
    }

    fun loadDefaultDate() {
        _selectedDate.postValue(Event(Calendar.getInstance()))
    }

    fun setTime(datetime: Calendar, type: TimeType) {
        when(type){
            TimeType.FOR_START -> _selectedStartTime.postValue(Event(datetime))

            TimeType.FOR_END -> _selectedEndTime.postValue(Event(datetime))
        }
    }

    fun onSubmitButtonClicked(activity: FragmentActivity){
        val user = authentication.getCurrentUser()?: return

        val blockedApplication = getBlockedApplication(activity)?: return

        loqService.addLoq(user.uid, blockedApplication)
                .ioToMain()
                .subscribe({ application ->
                    _addedBlockedApplication.postValue(Event(application))
                }, { error ->
                    ErrorDialog.show(activity, error)
                })
                .attachLifecycle(activity)
    }

    private fun getBlockedApplication(activity: FragmentActivity): BlockedApplication? {
        val user = authentication.getCurrentUser()?: return null

        val date = _selectedDate.value?.peekContent()
        val startTime = selectedStartTime.value?.peekContent()
        val endTime = selectedEndTime.value?.peekContent()
        val application = selectedApplication.value?.peekContent()
        if (startTime == null){
            ErrorDialog.show(activity, Throwable("You must select a start time"))
            return null
        }
        if (endTime == null){
            ErrorDialog.show(activity, Throwable("You must select an end time"))
            return null
        }

        if (application == null){
            ErrorDialog.show(activity, Throwable("You must select an application"))
            return null
        }

        val dayString = date!!.dayOfWeekString

        val startHour = startTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = startTime.get(Calendar.MINUTE)

        val endHour = endTime.get(Calendar.HOUR_OF_DAY)
        val endMinute = endTime.get(Calendar.MINUTE)

        val blockedDay = BlockedDay(
                dayString,
                date.get(Calendar.DAY_OF_MONTH),
                date.get(Calendar.MONTH),
                BlockTime(
                        startHour,
                        startMinute,
                        endHour,
                        endMinute
                )
        )

        for (currentBlockedApplication in blockedApplications){
            val appName = application.getAppName(activity.packageManager)
            if (currentBlockedApplication.applicationName.contentEquals(appName)){
                currentBlockedApplication.blockBlockedDays.add(blockedDay)
                return currentBlockedApplication
            }
        }

        return null
    }

    var dateForCalender: Calendar = selectedDate.value?.peekContent()?: Calendar.getInstance()

}