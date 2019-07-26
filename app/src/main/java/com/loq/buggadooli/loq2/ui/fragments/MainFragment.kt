package com.loq.buggadooli.loq2.ui.fragments

import android.os.Handler
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

import com.loq.buggadooli.loq2.R
import com.loq.buggadooli.loq2.extensions.inflateTo
import com.loq.buggadooli.loq2.extensions.replaceFragment
import com.loq.buggadooli.loq2.extensions.safeActivity
import com.loq.buggadooli.loq2.extensions.toast
import com.loq.buggadooli.loq2.ui.viewmodels.MainViewModel
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
