package com.easystreetinteractive.loq.ui.viewmodels

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.easystreetinteractive.loq.ads.AdManager
import com.easystreetinteractive.loq.billing.BillingManager
import com.easystreetinteractive.loq.loqer.CheckForLoqService
import com.easystreetinteractive.loq.preferences.PreferenceManager
import com.easystreetinteractive.loq.utils.Event
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import java.lang.Exception

class LoqScreenViewModel(
        private val preferenceManager: PreferenceManager,
        private val adManager: AdManager,
        private val billingManager: BillingManager
): ViewModel(), RewardedVideoAdListener{

    val showProgress: LiveData<Event<Boolean>> get() = _showProgress
    private val _showProgress = MutableLiveData<Event<Boolean>>()

    val rewarded: LiveData<Event<RewardItem?>> get() = _onRewarded
    private val _onRewarded = MutableLiveData<Event<RewardItem?>>()

    val isConnected = billingManager.isConnected

    val purchaseMade = billingManager.purchaseUpdated

    fun storeUnlockTime(){
        preferenceManager.storeTemporaryUnlockTime()
    }

    fun initializeAd(activity: Activity) {
        adManager.attach(activity)
        _showProgress.postValue(Event(true))
        adManager.initializeAd(this)
    }

    fun adClicked() {
        adManager.showAd()
    }

    override fun onRewardedVideoAdClosed() {
        _showProgress.postValue(Event(true))
        adManager.reloadAd()
    }

    override fun onRewardedVideoAdLeftApplication() {}

    override fun onRewardedVideoAdLoaded() {
        _showProgress.postValue(Event(false))
    }

    override fun onRewardedVideoAdOpened() {}

    override fun onRewardedVideoCompleted() {}

    override fun onRewarded(item: RewardItem?) {
        storeUnlockTime()
        _onRewarded.postValue(Event(item))
    }

    override fun onRewardedVideoStarted() {}

    override fun onRewardedVideoAdFailedToLoad(errorCode: Int) {
        if (errorCode == 0){
            _showProgress.postValue(Event(true))
            adManager.loadRewardedVideoAd()
        }
    }

    fun connectBilling() {
        billingManager.connect()
    }

    fun makePurchase(activity: Activity){
        billingManager.makePurchase(activity)
    }

    fun onPause(context: Context){
        adManager.onPause(context)
    }

    fun onResume(context: Context){
        adManager.onResume(context)
    }

    fun onDestroy(context: Context){
        adManager.onDestroy(context)
    }

}