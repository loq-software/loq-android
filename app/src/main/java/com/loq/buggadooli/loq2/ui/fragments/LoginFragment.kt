package com.loq.buggadooli.loq2.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.loq.buggadooli.loq2.R
import com.loq.buggadooli.loq2.extensions.addFragment
import com.loq.buggadooli.loq2.extensions.inflateTo
import com.loq.buggadooli.loq2.extensions.replaceFragment
import com.loq.buggadooli.loq2.extensions.safeActivity
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
            loginViewModel.loginWithFacebook(safeActivity, this)
        }

        btnGoogle.setOnClickListener {
            loginViewModel.loginWithGmail(safeActivity)
        }

        btnSubmit.setOnClickListener {
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
                safeActivity.replaceFragment(fragment = EasyLoqFragment())
            }
            else{
                Toast.makeText(safeActivity, "Invalid email or password", Toast.LENGTH_LONG).show()
            }
        })

        loginViewModel.signInError.observe(this, Observer { errorEvent ->
            val errorMessage = errorEvent.getContentIfNotHandled()
            if (errorMessage != null) {
                Toast.makeText(safeActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
        })
    }
}