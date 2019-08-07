package com.easystreetinteractive.loq.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode.OK
import com.android.billingclient.api.BillingClient.BillingResponseCode.USER_CANCELED
import com.easystreetinteractive.loq.utils.Event

interface BillingManager {

    val isConnected: MutableLiveData<Boolean>
    val purchaseUpdated: MutableLiveData<Event<Purchase>>

    fun connect()

    fun makePurchase(activity: Activity)

}

class RealBillingManager(private val context: Context): BillingManager{

    override val purchaseUpdated = MutableLiveData<Event<Purchase>>()

    override val isConnected = MutableLiveData<Boolean>()
            .apply {
                postValue(false)
            }


    private val listener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == OK && purchases != null) {
            val purchase = purchases.firstOrNull()
            purchase?.let {
                handlePurchase(purchase)

            }
        } else if (billingResult.responseCode == USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            // Handle any other error codes.
        }
    }

    private val stateListener = StateListener()

    private val billingClient = BillingClient.newBuilder(context)
            .enablePendingPurchases()
            .setListener(listener)
            .build()

    override fun connect() {
        billingClient.startConnection(stateListener)
    }

    override fun makePurchase(activity: Activity) {
        loadSkuDetails(SkuDetailsResponseListener { billingResult, skuDetailsList ->
            val responseCode = billingResult.responseCode
            if (responseCode == OK && skuDetailsList != null) {

                val details = skuDetailsList.firstOrNull()
                details?.let {
                    val flowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(it)
                            .build()
                    val responseCode = billingClient.launchBillingFlow(activity, flowParams)
                }
            }
        })
    }

    private fun loadSkuDetails(listener: SkuDetailsResponseListener) {
        val skuList = ArrayList<String>()
        //Todo: Add items from our google play account.
        skuList.add("premium_upgrade")
        skuList.add("gas")
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        billingClient.querySkuDetailsAsync(params.build(), listener)
    }

    private fun handlePurchase(purchase: Purchase) {
        val purchaseState = purchase.purchaseState
        if (purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, object : AcknowledgePurchaseResponseListener {
                    override fun onAcknowledgePurchaseResponse(billingResult: BillingResult?) {
                        purchaseUpdated.postValue(Event(purchase))
                    }
                })
            }
            else{
                purchaseUpdated.postValue(Event(purchase))
            }
        }
        else{
            Log.d("test", "$purchaseState")
        }
    }

    private inner class StateListener: BillingClientStateListener{

        override fun onBillingServiceDisconnected() {
            isConnected.postValue(false)
        }

        override fun onBillingSetupFinished(billingResult: BillingResult) {
            if (billingResult.responseCode == OK) {
                isConnected.postValue(true)
            }
        }

    }
    
}