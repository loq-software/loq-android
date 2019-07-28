package com.loq.buggadooli.loq2.ui.viewmodels

import android.content.pm.ApplicationInfo
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.loq.buggadooli.loq2.R
import com.loq.buggadooli.loq2.extensions.disposeOnDetach
import com.loq.buggadooli.loq2.extensions.ioToMain
import com.loq.buggadooli.loq2.extensions.subscribeForOutcome
import com.loq.buggadooli.loq2.network.Outcome
import com.loq.buggadooli.loq2.repositories.ApplicationsRepository
import com.loq.buggadooli.loq2.utils.Event

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