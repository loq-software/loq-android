package com.loq.buggadooli.loq2.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.loq.buggadooli.loq2.R
import com.loq.buggadooli.loq2.extensions.*
import com.loq.buggadooli.loq2.ui.viewmodels.LoginViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LoginFragment: Fragment() {

    private val loginViewModel by sharedViewModel<LoginViewModel>()

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
                loginViewModel.loadLoqs(view)
            }
            else{
                Toast.makeText(safeActivity, "Invalid email or password", Toast.LENGTH_LONG).show()
            }
            progressLayout.hide()
        })

        loginViewModel.signInError.observe(this, Observer { errorEvent ->
            val errorMessage = errorEvent.getContentIfNotHandled()
            if (errorMessage != null) {
                Toast.makeText(safeActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
            progressLayout.hide()
        })

        loginViewModel.onLoqsLoaded.observe(this, Observer { event ->
            val loqs = event.getContentIfNotHandled()
            loqs?.let{
                if (it.isNotEmpty()){
                    safeActivity.replaceFragment(fragment = DashboardFragment())
                }
                else {
                    safeActivity.replaceFragment(fragment = EasyLoqFragment())
                }
            }
        })

        loginViewModel.showErrorMessage.observe(this, Observer { event ->
            val message = event.getContentIfNotHandled()
            message?.let {
                safeActivity.toast(message)
            }
        })
    }
}