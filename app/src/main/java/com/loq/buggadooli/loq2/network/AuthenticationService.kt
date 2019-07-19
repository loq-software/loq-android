package com.loq.buggadooli.loq2.network

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import io.reactivex.Observable

interface AuthenticationService{

    fun createUserWithEmailAndPassword(email: String, password: String)

    fun signInWithEmailAndPassword(email: String, password: String):Observable<AuthenticationResult>

    fun signInWithGoogle(activity: Activity)

    fun handleGoogleLogin(data: Intent?): Observable<AuthenticationResult>

    fun signInWithFacebook()

    val currentUser: FirebaseUser?
}

class RealAuthenticationService(
        private val authentication: FirebaseAuth,
        private val facebookService: FacebookService,
        private val gmailService: GmailService

): AuthenticationService{

    override val currentUser: FirebaseUser?
        get() = authentication.currentUser


    override fun signInWithGoogle(activity: Activity) {
        gmailService.signIn(activity)
    }

    override fun signInWithFacebook() {
    }

    override fun signInWithEmailAndPassword(email: String, password: String): Observable<AuthenticationResult> {

        return Observable.create { emitter ->
            authentication.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = authentication.currentUser
                            val authenticationResult = AuthenticationResult(user)
                            emitter.onNext(authenticationResult)
                        } else {
                            emitter.onError(task.exception?: Throwable("Could not log in"))

                        }
                    }
        }

    }

    override fun createUserWithEmailAndPassword(email: String, password: String) {
        authentication.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener{ task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = authentication.currentUser
                       // sendEmailVerification(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        //Toast.makeText(applicationContext, "Failed to register user " + task.exception!!.toString(), Toast.LENGTH_LONG).show()
                    }

                    // ...
                }
    }

    override fun handleGoogleLogin(data: Intent?): Observable<AuthenticationResult> {
        return Observable.create { emitter ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                val token = account?.idToken
                val id = account?.id
                val credential = GoogleAuthProvider.getCredential(token, null)
                authentication.signInWithCredential(credential)
                        .addOnCompleteListener { resultTask ->
                            if (resultTask.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithCredential:success")
                                val user = authentication.currentUser
                                user?.let { emitter.onNext(AuthenticationResult(it)) }
                            } else {
                                val error = "signInWithCredential:failure"
                                Log.w(TAG, error, resultTask.exception)
                                emitter.onError(Throwable(error))
                            }

                        }
            } catch (e: Exception) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                emitter.onError(Throwable("Google sign in failed"))
            }
        }
    }

}

data class AuthenticationResult(val user: FirebaseUser? = null)