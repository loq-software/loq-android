package com.loq.buggadooli.loq2.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.loq.buggadooli.loq2.R
import com.loq.buggadooli.loq2.databinding.MainActivityBinding
import com.loq.buggadooli.loq2.extensions.replaceFragment
import com.loq.buggadooli.loq2.extensions.setDataBindingContentView
import com.loq.buggadooli.loq2.loqer.CheckForLoqService
import com.loq.buggadooli.loq2.ui.fragments.MainFragment
import com.loq.buggadooli.loq2.ui.viewmodels.LoginViewModel
import com.loq.buggadooli.loq2.ui.viewmodels.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.Exception

class MainActivity: AppCompatActivity() {

    private lateinit var binding: MainActivityBinding
    private val loginViewModel by viewModel<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = setDataBindingContentView(R.layout.main_activity)
        replaceFragment(fragment = MainFragment())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loginViewModel.onActivityResult(requestCode, resultCode, data, this)
    }

    override fun onResume() {
        super.onResume()
        try {
            stopService(CheckForLoqService.getIntent(this))
        }
        catch (exception: Exception){
            exception.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            application.startService(CheckForLoqService.getIntent(this))
        }
        catch (exception: Exception){
            exception.printStackTrace()
        }
    }
}