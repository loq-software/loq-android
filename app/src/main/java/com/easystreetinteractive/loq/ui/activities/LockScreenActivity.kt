package com.easystreetinteractive.loq.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer

import com.easystreetinteractive.loq.R
import com.easystreetinteractive.loq.extensions.showOrHide
import com.easystreetinteractive.loq.ui.viewmodels.LoqScreenViewModel
import kotlinx.android.synthetic.main.activity_loq_screen.*

import org.koin.androidx.viewmodel.ext.android.viewModel

class LockScreenActivity : AppCompatActivity() {

    private val viewModel by viewModel<LoqScreenViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loq_screen)

        viewModel.initializeAd(this)

        imgAd.setOnClickListener {
            viewModel.adClicked()
        }
        btnPay.setOnClickListener {
            viewModel.makePurchase(this)
        }

        viewModel.showProgress.observe(this, Observer { event ->
            val show = event.getContentIfNotHandled()
            show?.let {
                progressLayout.showOrHide(show)
            }
        })

        viewModel.rewarded.observe(this, Observer { event ->
            val reward = event.getContentIfNotHandled()
            reward?.let {
                finish()
            }
        })

        viewModel.isConnected.observe(this, Observer { connected ->
            if (!connected){
                viewModel.connectBilling()
            }
        })

        viewModel.purchaseMade.observe(this, Observer { event ->
            val purchase = event.getContentIfNotHandled()
            purchase?.let {
                viewModel.storeUnlockTime()
                finish()
            }
        })
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause(this)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy(this)
    }
}
