package com.easystreetinteractive.loq.network.api

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.easystreetinteractive.loq.constants.Constants
import io.reactivex.Observable

interface AuthenticationService{

    fun signInWithEmailAndPassword(email: String, password: String):Observable<AuthenticationResult>

    fun signInWithGoogle(activity: Activity)

    fun handleGoogleLogin(data: Intent?): Observable<AuthenticationResult>

    fun signInWithFacebook(activity: Activity)

    fun handleFacebookLogin(token: AccessToken): Observable<AuthenticationResult>

    fun getCurrentUser(): FirebaseUser?

    fun logout()
}

class RealAuthenticationService(
        private val authentication: FirebaseAuth,
        private val options: GoogleSignInOptions

): AuthenticationService {

    override fun getCurrentUser(): FirebaseUser? {
        return authentication.currentUser
    }


    override fun signInWithGoogle(activity: Activity) {
        val client = GoogleSignIn.getClient(activity, options)
        val signInIntent = client!!.signInIntent
        activity.startActivityForResult(signInIntent, Constants.GOOGLE_LOGIN_REQUEST)
    }

    override fun signInWithFacebook(activity: Activity) {
        LoginManager.getInstance().logInWithReadPermissions(activity, listOf("public_profile", "email"))
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

    override fun handleGoogleLogin(data: Intent?): Observable<AuthenticationResult> {
        return Observable.create { emitter ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val errorMessage = "Google sign in failed"

            try {
                val account = task.getResult(ApiException::class.java)
                val token = account?.idToken
                val credential = GoogleAuthProvider.getCredential(token, null)
                authentication.signInWithCredential(credential)
                        .addOnCompleteListener { resultTask ->
                            if (resultTask.isSuccessful) {
                                Log.d(TAG, "signInWithCredential:success")
                                val user = authentication.currentUser
                                if (user != null){
                                    emitter.onNext(AuthenticationResult(user))
                                }
                                else{
                                    emitter.onError(Throwable(errorMessage))
                                }
                                user?.let { emitter.onNext(AuthenticationResult(it)) }
                            } else {
                                Log.w(TAG, errorMessage, resultTask.exception)
                                emitter.onError(Throwable(errorMessage))
                            }

                        }
            } catch (e: Exception) {
                Log.w(TAG, errorMessage, e)
                emitter.onError(Throwable(errorMessage))
            }
        }
    }

    override fun handleFacebookLogin(token: AccessToken): Observable<AuthenticationResult> {
        return Observable.create{ emitter ->
            val credential = FacebookAuthProvider.getCredential(token.token)
            authentication.signInWithCredential(credential)
                    .addOnCompleteListener{ task ->
                        val errorMessage = "Facebook authentication failed."
                        if (task.isSuccessful) {
                            val user = authentication.currentUser
                            if (user != null) {
                                emitter.onNext(AuthenticationResult(user))
                            }
                            else{
                                emitter.onError(Throwable(errorMessage))
                            }
                        } else {
                            emitter.onError(Throwable(errorMessage))
                        }

                    }

        }
    }

    override fun logout() {
        authentication.signOut()
    }

}

data class AuthenticationResult(val user: FirebaseUser? = null)