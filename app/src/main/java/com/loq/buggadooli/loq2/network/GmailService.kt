package com.loq.buggadooli.loq2.network

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.loq.buggadooli.loq2.constants.Constants

interface GmailService{

    fun signIn(activity: Activity)
}

class RealGmailService(private val options: GoogleSignInOptions): GmailService {

    override fun signIn(activity: Activity) {
        val client = GoogleSignIn.getClient(activity, options)
        val signInIntent = client!!.signInIntent
        activity.startActivityForResult(signInIntent, Constants.GOOGLE_LOGIN_REQUEST)
    }
}