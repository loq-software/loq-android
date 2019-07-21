package com.loq.buggadooli.loq2.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.loq.buggadooli.loq2.R
import com.loq.buggadooli.loq2.extensions.inflateTo
import com.loq.buggadooli.loq2.extensions.safeActivity
import kotlinx.android.synthetic.main.fragment_registration.*

class RegistrationFragment : Fragment() {

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflateTo(R.layout.fragment_registration, container)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initGoogleSignin()
        mAuth = FirebaseAuth.getInstance()
        btnSubmit!!.setOnClickListener { registerWithEmailPassword() }
    }

    private fun initGoogleSignin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(safeActivity, gso)
    }

    private fun registerWithEmailPassword() {
        if (validateCredentials()) {
            createFirebaseUser(txtEmail!!.text.toString(), txtPassword!!.text.toString())
        }
    }

    private fun createFirebaseUser(email: String, password: String) {
        mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        //Log.d(TAG, "createUserWithEmail:success");
                        val user = mAuth!!.currentUser
                        sendEmailVerification(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(safeActivity, "Failed to register user " + task.exception!!.toString(), Toast.LENGTH_LONG).show()
                    }

                    // ...
                }
    }

    private fun sendEmailVerification(fbuser: FirebaseUser?) {
        fbuser!!.sendEmailVerification()
                .addOnCompleteListener{ task ->
                    if (task.isSuccessful) {
                        Toast.makeText(safeActivity,
                                "Verification email sent to " + fbuser.email!!,
                                Toast.LENGTH_SHORT).show()
                        safeActivity.onBackPressed()
                    } else {
                        //Log.e(TAG, "sendEmailVerification", task.getException());
                        Toast.makeText(safeActivity,
                                "Failed to send verification email.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun validateCredentials(): Boolean {
        val pswd = txtPassword!!.text.toString()
        val pswd2 = txtRepeatPassword!!.text.toString()
        val email = txtEmail!!.text.toString()
        if (!email.isEmpty() && !pswd.isEmpty() && !pswd2.isEmpty()) {
            if (pswd == pswd2) {
                return true
            } else {
                Toast.makeText(safeActivity, "Passwords do not match", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(safeActivity, "Please enter a proper email address and password", Toast.LENGTH_LONG).show()
        }
        return false
    }
}
