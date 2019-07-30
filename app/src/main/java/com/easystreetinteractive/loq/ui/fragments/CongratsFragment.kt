package com.easystreetinteractive.loq.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.easystreetinteractive.loq.R

import com.easystreetinteractive.loq.extensions.inflateTo
import com.easystreetinteractive.loq.extensions.replaceFragment
import com.easystreetinteractive.loq.extensions.safeActivity
import kotlinx.android.synthetic.main.fragment_congrats.*

class CongratsFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflateTo(R.layout.fragment_congrats, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnDashboard.setOnClickListener { safeActivity.replaceFragment(fragment = DashboardFragment()) }
        btnTwitterShare.setOnClickListener { shareOnTwitter() }
    }

    private fun shareOnTwitter() {
        val tweet = Intent(Intent.ACTION_VIEW)
        tweet.data = Uri.parse("http://twitter.com/?status=" + Uri.encode("I am now living #LifeWithLoq on Android!"))//where message is your string message
        startActivity(tweet)
    }
}
