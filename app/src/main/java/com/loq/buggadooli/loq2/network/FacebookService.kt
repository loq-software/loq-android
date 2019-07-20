package com.loq.buggadooli.loq2.network

import android.app.Activity
import com.facebook.login.LoginManager

interface FacebookService{
    fun loginWithReadPermissions(activity: Activity)
}

class RealFacebookService(): FacebookService{

    override fun loginWithReadPermissions(activity: Activity) {
        LoginManager.getInstance().logInWithReadPermissions(activity, listOf("public_profile", "email"))
    }
}