package com.easystreetinteractive.loq.ui.viewmodels

import android.content.pm.ApplicationInfo
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.easystreetinteractive.loq.extensions.disposeOnDetach
import com.easystreetinteractive.loq.extensions.ioToMain
import com.easystreetinteractive.loq.extensions.subscribeForOutcome
import com.easystreetinteractive.loq.network.Outcome
import com.easystreetinteractive.loq.repositories.ApplicationsRepository
import com.easystreetinteractive.loq.utils.Event

class CustomLoqViewModel(private val repository: ApplicationsRepository): ViewModel() {

    val onApplicationsLoaded: LiveData<Event<List<ApplicationInfo>>> get() = _onApplicationsLoaded
    private val _onApplicationsLoaded = MutableLiveData<Event<List<ApplicationInfo>>>()

    fun loadApplications(view: View) {
        repository.getInstalledApps()
                .ioToMain()
                .subscribeForOutcome { outcome ->
                    when(outcome){
                        is Outcome.Success ->{
                            val apps = outcome.data
                            _onApplicationsLoaded.postValue(Event(apps))
                        }
                    }
                }
                .disposeOnDetach(view)
    }

}