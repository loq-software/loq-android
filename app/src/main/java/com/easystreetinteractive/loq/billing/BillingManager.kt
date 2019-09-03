package com.easystreetinteractive.loq.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode.OK
import com.android.billingclient.api.BillingClient.BillingResponseCode.USER_CANCELED
import com.easystreetinteractive.loq.R
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
           Log.e(TAG, billingResult.debugMessage?: billingResult.responseCode.toString())
        } else {
            Log.e(TAG, billingResult.debugMessage?: billingResult.responseCode.toString())
        }
    }

    private val stateListener = object : BillingClientStateListener {
        override fun onBillingServiceDisconnected() {
            isConnected.postValue(false)
        }

        override fun onBillingSetupFinished(billingResult: BillingResult) {
            if (billingResult.responseCode == OK) {
                isConnected.postValue(true)
            }
        }
    }

    private val billingClient = BillingClient.newBuilder(context)
            .enablePendingPurchases()
            .setListener(listener)
            .build()

    override fun connect() {
        billingClient.startConnection(stateListener)
    }

    override fun makePurchase(activity: Activity) {
        loadSkuDetails(SkuDetailsResponseListener { billingResult, skuDetailsList ->
            if (billingResult.responseCode == OK && skuDetailsList != null) {

                val details = skuDetailsList.firstOrNull()
                details?.let {
                    val flowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(it)
                            .build()
                    val responseCode = billingClient.launchBillingFlow(activity, flowParams)
                    Log.e(TAG, responseCode.debugMessage?: responseCode.responseCode.toString())
                }
            }
        })
    }

    private fun loadSkuDetails(listener: SkuDetailsResponseListener) {
        val skuList = ArrayList<String>()
        skuList.add(context.getString(R.string.in_app_purchase_product_id))
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        billingClient.querySkuDetailsAsync(params.build(), listener)
    }

    private fun handlePurchase(purchase: Purchase) {
        val purchaseState = purchase.purchaseState
        if (purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {

                val consumeParams =
                        ConsumeParams.newBuilder()
                                .setPurchaseToken(purchase.purchaseToken)
                                .setDeveloperPayload(purchase.developerPayload)
                                .build()

                billingClient.consumeAsync(consumeParams, object: ConsumeResponseListener{
                    override fun onConsumeResponse(billingResult: BillingResult?, purchaseToken: String?) {
                        purchaseUpdated.postValue(Event(purchase))
                    }

                })
            }
            else{
                purchaseUpdated.postValue(Event(purchase))
            }
        }
        else{
            Log.d(TAG, "$purchaseState")
        }
    }

    companion object{
        const val TAG = "BillingManager"
    }
    
}