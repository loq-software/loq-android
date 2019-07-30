package com.easystreetinteractive.loq.ui.fragments

import android.os.Handler
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.easystreetinteractive.loq.R

import com.easystreetinteractive.loq.extensions.inflateTo
import com.easystreetinteractive.loq.extensions.replaceFragment
import com.easystreetinteractive.loq.extensions.safeActivity
import com.easystreetinteractive.loq.extensions.toast
import com.easystreetinteractive.loq.ui.activities.MainActivity
import com.easystreetinteractive.loq.ui.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.main_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MainFragment : Fragment() {

    private var progressStatus = 0
    private val handler = Handler()
    private val mainViewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflateTo(R.layout.main_fragment, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Thread(Runnable {
            while (progressStatus < 100) {
                progressStatus += 1
                // Update the progress bar and display the
                //current value in the text view
                handler.post { progressBar!!.progress = progressStatus }
                try {
                    Thread.sleep(50)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }

            if (mainViewModel.user == null) {
                safeActivity.replaceFragment( fragment = LoginFragment())
            } else {
                val hasStatsPermission = (safeActivity as MainActivity).permissionsManager.hasUsageStatsPermission()
                mainViewModel.loadLoqs(view)
            }

        }).start()

        mainViewModel.onLoqsLoaded.observe(this, Observer { event ->
            val loqs = event.getContentIfNotHandled()
            loqs?.let {
                if (loqs.isEmpty()){
                    safeActivity.replaceFragment(fragment = EasyLoqFragment())
                    return@Observer
                }
                safeActivity.replaceFragment(fragment = DashboardFragment())
            }

        })

        mainViewModel.showError.observe(this, Observer { event ->
            val message = event.getContentIfNotHandled()
            message?.let {
                safeActivity.toast(it)
            }
        })
    }

}
