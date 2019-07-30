package com.easystreetinteractive.loq.ui.viewmodels

import android.content.pm.ApplicationInfo
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.easystreetinteractive.loq.R
import com.easystreetinteractive.loq.extensions.disposeOnDetach
import com.easystreetinteractive.loq.extensions.ioToMain
import com.easystreetinteractive.loq.extensions.subscribeForOutcome
import com.easystreetinteractive.loq.network.Outcome
import com.easystreetinteractive.loq.repositories.ApplicationsRepository
import com.easystreetinteractive.loq.utils.Event

class EasyLoqViewModel(private val repository: ApplicationsRepository): ViewModel() {

    val installedPopularApplications: LiveData<Event<List<ApplicationInfo>>> get() = _installedPopularApplications
    private val _installedPopularApplications = MutableLiveData<Event<List<ApplicationInfo>>>()

    fun buttonNextForPopularClicked(view: View) {
        val popularApps = view.resources.getStringArray(R.array.popular_apps_2)
        repository.getInstalledPopularApps(popularApps)
                .ioToMain()
                .subscribeForOutcome { outcome ->
                    when(outcome){
                        is Outcome.Success ->{
                            val success = outcome.data
                            _installedPopularApplications.postValue(Event(success))
                        }
                    }
                }
                .disposeOnDetach(view)
    }
}