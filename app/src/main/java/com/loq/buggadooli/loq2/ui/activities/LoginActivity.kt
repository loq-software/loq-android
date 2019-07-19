package com.loq.buggadooli.loq2.ui.activities

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.loq.buggadooli.loq2.R
import com.loq.buggadooli.loq2.ui.viewmodels.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity: AppCompatActivity() {

    private val loginViewModel by viewModel<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar!!.hide()
        btnFacebook.setOnClickListener {
            loginViewModel.loginWithFacebook()
        }

        btnFacebook.setOnClickListener {
            loginViewModel.loginWithGmail()
        }

        btnSubmit.setOnClickListener {
            loginViewModel.loginWithEmail(txtEmail.text.toString(), txtPassword.text.toString())
        }
    }
}