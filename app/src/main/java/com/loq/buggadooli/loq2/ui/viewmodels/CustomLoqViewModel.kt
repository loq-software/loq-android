package com.loq.buggadooli.loq2.ui.viewmodels

import android.content.pm.ApplicationInfo
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.loq.buggadooli.loq2.extensions.disposeOnDetach
import com.loq.buggadooli.loq2.extensions.ioToMain
import com.loq.buggadooli.loq2.extensions.subscribeForOutcome
import com.loq.buggadooli.loq2.models.CustomLoqItem
import com.loq.buggadooli.loq2.network.Outcome
import com.loq.buggadooli.loq2.repositories.ApplicationsRepository
import com.loq.buggadooli.loq2.utils.Event

class CustomLoqViewModel(private val repository: ApplicationsRepository): ViewModel() {

    val onApplicationsLoaded: LiveData<Event<List<ApplicationInfo>>> get() = _onApplicationsLoaded
    private val _onApplicationsLoaded = MutableLiveData<Event<List<ApplicationInfo>>>()

    fun onNextButtonClicked(applicationItems: List<CustomLoqItem>) {

    }

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