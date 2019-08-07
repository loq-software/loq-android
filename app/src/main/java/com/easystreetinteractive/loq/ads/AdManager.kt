package com.easystreetinteractive.loq.ads

import android.app.Activity
import android.content.Context
import com.easystreetinteractive.loq.R
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener

interface AdManager {

    fun attach(activity: Activity)

    fun initializeAd(listener: RewardedVideoAdListener)

    fun loadRewardedVideoAd()

    fun showAd()

    fun reloadAd()

    fun onPause(context: Context)

    fun onResume(context: Context)

    fun onDestroy(context: Context)

}

class RealAdManager(): AdManager {

    private lateinit var mRewardedVideoAd: RewardedVideoAd

    private var activity: Activity? = null

    override fun attach(activity: Activity) {
        this.activity = activity
    }

    override fun initializeAd(listener: RewardedVideoAdListener) {
        /* // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713")*/
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(activity)
        mRewardedVideoAd.rewardedVideoAdListener = listener
        loadRewardedVideoAd()
    }

    override fun loadRewardedVideoAd() {
        val videoLink = activity?.resources?.getString(R.string.ad_video_link)?: return
        mRewardedVideoAd.loadAd(videoLink,
                PublisherAdRequest.Builder()
                        .addTestDevice("6498DCA9BBB5CA9E79ECDA603C8A440F")
                        .build())
    }

    override fun showAd() {
        if (mRewardedVideoAd.isLoaded) {
            mRewardedVideoAd.show()
        }
    }

    override fun reloadAd() {
        if (! mRewardedVideoAd.isLoaded) {
            loadRewardedVideoAd()
        }
    }

    override fun onPause(context: Context) {
        mRewardedVideoAd.pause(context)
    }

    override fun onResume(context: Context) {
        mRewardedVideoAd.resume(context)
    }

    override fun onDestroy(context: Context) {
        mRewardedVideoAd.destroy(context)
        activity = null
    }
}