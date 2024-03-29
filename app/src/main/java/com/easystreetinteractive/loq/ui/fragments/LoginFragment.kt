package com.easystreetinteractive.loq.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.easystreetinteractive.loq.R
import com.easystreetinteractive.loq.extensions.*
import com.easystreetinteractive.loq.ui.viewmodels.LoginViewModel
import com.easystreetinteractive.loq.ui.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LoginFragment: Fragment() {

    private val loginViewModel by sharedViewModel<LoginViewModel>()
    private val mainViewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflateTo(R.layout.fragment_login, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnFacebook.setOnClickListener {
            progressLayout.show()
            loginViewModel.loginWithFacebook(safeActivity, this)
        }

        btnGoogle.setOnClickListener {
            progressLayout.show()
            loginViewModel.loginWithGmail(safeActivity)
        }

        btnSubmit.setOnClickListener {
            progressLayout.show()
            loginViewModel.loginWithEmail(txtEmail.text.toString(), txtPassword.text.toString(), this)
        }

        registerButton.setOnClickListener {
            safeActivity.addFragment(fragment = RegistrationFragment())
        }

        loginViewModel.signInSuccessful.observe(this, Observer { userEvent ->
            val event = userEvent.getContentIfNotHandled()
            event?: return@Observer
            val user = event.user
            if (user != null){
                progressLayout.show()
                mainViewModel.loadLoqs(safeActivity)
            }
            else{
                progressLayout.hide()
                Toast.makeText(safeActivity, "Invalid email or password", Toast.LENGTH_LONG).show()
            }
        })

        loginViewModel.signInError.observe(this, Observer { errorEvent ->
            val errorMessage = errorEvent.getContentIfNotHandled()
            if (errorMessage != null) {
                Toast.makeText(safeActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
            progressLayout.hide()
        })

        mainViewModel.onLoqsLoaded.observe(this, Observer { event ->
            val loqs = event.getContentIfNotHandled()
            loqs?.let{
                if (it.isNotEmpty()){
                    safeActivity.replaceFragment(fragment = DashboardFragment())
                }
                else {
                    safeActivity.replaceFragment(fragment = EasyLoqFragment())
                }
            }
            progressLayout.hide()
        })

        loginViewModel.showErrorMessage.observe(this, Observer { event ->
            val message = event.getContentIfNotHandled()
            message?.let {
                safeActivity.toast(message)
            }
            progressLayout.hide()
        })
    }
}