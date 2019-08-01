package com.easystreetinteractive.loq.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.easystreetinteractive.loq.R
import com.easystreetinteractive.loq.databinding.MainActivityBinding
import com.easystreetinteractive.loq.extensions.replaceFragment
import com.easystreetinteractive.loq.extensions.setDataBindingContentView
import com.easystreetinteractive.loq.loqer.CheckForLoqService
import com.easystreetinteractive.loq.permissions.PermissionsManager
import com.easystreetinteractive.loq.ui.fragments.MainFragment
import com.easystreetinteractive.loq.ui.viewmodels.LoginViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.Exception

class MainActivity: AppCompatActivity() {

    private lateinit var binding: MainActivityBinding
    private val loginViewModel by viewModel<LoginViewModel>()
    val permissionsManager by inject<PermissionsManager>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionsManager.attach(this)
        binding = setDataBindingContentView(R.layout.main_activity)
        replaceFragment(fragment = MainFragment())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loginViewModel.onActivityResult(requestCode, resultCode, data, this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionsManager.processResult(requestCode, permissions, grantResults)
    }

    override fun onResume() {
        super.onResume()
        try {
            val intent = CheckForLoqService.getIntent(this)
            intent?.let {
                stopService(intent)
            }
        }
        catch (exception: Exception){
            exception.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            val intent = CheckForLoqService.getIntent(this)
            intent?.let {
                application.startService(intent)
            }
        }
        catch (exception: Exception){
            exception.printStackTrace()
        }
    }

    override fun onDestroy() {
        permissionsManager.detach(this)
        super.onDestroy()
    }
}