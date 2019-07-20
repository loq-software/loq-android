package com.loq.buggadooli.loq2.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
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
            loginViewModel.loginWithFacebook(this, this)
        }

        btnGoogle.setOnClickListener {
            loginViewModel.loginWithGmail(this)
        }

        btnSubmit.setOnClickListener {
            loginViewModel.loginWithEmail(txtEmail.text.toString(), txtPassword.text.toString(), this)
        }

        registerButton.setOnClickListener {
            val intent = Intent(applicationContext, RegistrationActivity::class.java)
            startActivity(intent)
        }

        loginViewModel.signInSuccessful.observe(this, Observer { userEvent ->
            val user = userEvent.getContentIfNotHandled()?.user
            if (user != null){
                openSetupActivity()
            }
            else{
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_LONG).show()
            }
        })

        loginViewModel.signInError.observe(this, Observer { errorEvent ->
            val errorMessage = errorEvent.getContentIfNotHandled()
            if (errorMessage != null) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun openSetupActivity() {
        val intent = Intent(applicationContext, SetupActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loginViewModel.onActivityResult(requestCode, resultCode, data, this)
    }
}