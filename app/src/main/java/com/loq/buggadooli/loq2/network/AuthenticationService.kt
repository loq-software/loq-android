package com.loq.buggadooli.loq2.network

import com.google.firebase.auth.FirebaseAuth

interface AuthenticationService{

    fun createUserWithEmailAndPassword(email: String, password: String)

    fun signInWithEmailAndPassword(email: String, password: String)

    fun signInWithGoogle()

    fun signInWithFacebook()
}

class RealAuthenticationService(
        private val auth: FirebaseAuth,
        private val facebookService: FacebookService,
        private val gmailService: GmailService

): AuthenticationService{

    override fun signInWithGoogle() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun signInWithFacebook() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun signInWithEmailAndPassword(email: String, password: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createUserWithEmailAndPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener{ task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser
                       // sendEmailVerification(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        //Toast.makeText(applicationContext, "Failed to register user " + task.exception!!.toString(), Toast.LENGTH_LONG).show()
                    }

                    // ...
                }
    }


}