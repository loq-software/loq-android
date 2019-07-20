package com.loq.buggadooli.loq2.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.loq.buggadooli.loq2.R
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        supportActionBar!!.hide()
        initGoogleSignin()
        mAuth = FirebaseAuth.getInstance()
        btnSubmit!!.setOnClickListener { registerWithEmailPassword() }
    }

    private fun initGoogleSignin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            openSetupActivity()
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            //Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(applicationContext, "Sign In With Google Failed! Status code: " + e.statusCode, Toast.LENGTH_LONG).show()
        }

    }

    private fun openSetupActivity() {
        val intent = Intent(applicationContext, SetupActivity::class.java)
        startActivity(intent)
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
                        Toast.makeText(applicationContext, "Failed to register user " + task.exception!!.toString(), Toast.LENGTH_LONG).show()
                    }

                    // ...
                }
    }

    private fun sendEmailVerification(fbuser: FirebaseUser?) {
        fbuser!!.sendEmailVerification()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext,
                                "Verification email sent to " + fbuser.email!!,
                                Toast.LENGTH_SHORT).show()
                        onBackPressed()
                    } else {
                        //Log.e(TAG, "sendEmailVerification", task.getException());
                        Toast.makeText(applicationContext,
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
                Toast.makeText(applicationContext, "Passwords do not match", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(applicationContext, "Please enter a proper email address and password", Toast.LENGTH_LONG).show()
        }
        return false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity() // or finish();
    }

    companion object {
        private val RC_SIGN_IN = 0
    }
}
