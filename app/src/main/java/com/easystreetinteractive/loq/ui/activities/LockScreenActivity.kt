package com.easystreetinteractive.loq.ui.activities

import android.app.ActivityManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

import com.android.vending.billing.IInAppBillingService
import com.easystreetinteractive.loq.R
import com.easystreetinteractive.loq.extensions.hide
import com.easystreetinteractive.loq.extensions.show
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import kotlinx.android.synthetic.main.activity_loq_screen.*

import org.json.JSONException
import org.json.JSONObject

class LockScreenActivity : AppCompatActivity(), RewardedVideoAdListener {
    private lateinit var mRewardedVideoAd: RewardedVideoAd
    private lateinit var imgAd: ImageView
    internal var inAppBillingService: IInAppBillingService? = null
    private var btnPay: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loq_screen)
        initAd()
        imgAd = findViewById(R.id.imgAd)
        imgAd.setOnClickListener {
            if (mRewardedVideoAd.isLoaded) {
                mRewardedVideoAd.show()
                //finish()
            }
            else{
                //loadRewardedVideoAd()
            }
        }
        initBilling()
        btnPay = findViewById(R.id.btnPay)
        btnPay!!.setOnClickListener { buyItem(UNLOCK_PURCHASE_PRODUCT) }
    }

    private fun initAd() {
       /* // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713")*/
        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this)
        mRewardedVideoAd.rewardedVideoAdListener = this
        loadRewardedVideoAd()
    }

    private fun loadRewardedVideoAd() {
        progressLayout.show()
        mRewardedVideoAd.loadAd(resources.getString(R.string.ad_video_link),
                PublisherAdRequest.Builder()
                        .addTestDevice("6498DCA9BBB5CA9E79ECDA603C8A440F")
                        .build())
    }

    private fun initBilling() {
        // Attach the service connection.

        // Bind the service.
        val serviceIntent = Intent("com.android.vending.billing.InAppBillingService.BIND")
        serviceIntent.setPackage("com.android.vending")
        serviceConnection?.let {
            bindService(serviceIntent, it, Context.BIND_AUTO_CREATE)
        }
    }

    private fun buyItem(productID: String) {
        if (inAppBillingService != null) {
            try {
                val buyIntentBundle = inAppBillingService!!.getBuyIntent(3, packageName, productID, "inapp", "bGoaFn4uQZbPiQJo4pf9RzJ+V7g/yqDXvKRqq+JT")
                val pendingIntent = buyIntentBundle.getParcelable<PendingIntent>("BUY_INTENT")
                startIntentSenderForResult(pendingIntent!!.intentSender, 1003, Intent(), 0, 0, 0)
            } catch (e: RemoteException) {
                e.printStackTrace()
            } catch (e: IntentSender.SendIntentException) {
                e.printStackTrace()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1003 && resultCode == RESULT_OK) {

            val purchaseData = data!!.getStringExtra("INAPP_PURCHASE_DATA")

            val thread = Thread(Runnable {
                try {
                    val jo = JSONObject(purchaseData)
                    // Get the productID of the purchased item.
                    val sku = jo.getString("productId")
                    if (sku == UNLOCK_PURCHASE_PRODUCT) {
                        inAppBillingService!!.consumePurchase(3, packageName, jo.getString("purchaseToken"))
                        Toast.makeText(this@LockScreenActivity, "Unloq time" + " is successfully purchased. Enjoy!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@LockScreenActivity, "Failed to make purchase.", Toast.LENGTH_LONG).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(this@LockScreenActivity, "Failed to make purchase.", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                } catch (e: RemoteException) {
                    Toast.makeText(this@LockScreenActivity, "Failed to make purchase.", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            })
            thread.start()
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.i("isMyServiceRunning?", true.toString() + "")
                return true
            }
        }
        Log.i("isMyServiceRunning?", false.toString() + "")
        return false
    }

    override fun onRewardedVideoAdLoaded() {
        Log.d("test", "onRewardedVideoAdLoaded()")
        progressLayout.hide()
    }

    override fun onRewardedVideoAdOpened() {
        Log.d("test", "onRewardedVideoAdOpened()")

    }

    override fun onRewardedVideoStarted() {
        Log.d("test", "onRewardedVideoStarted()")

    }

    override fun onRewardedVideoAdClosed() {
        Log.d("test", "onRewardedVideoAdClosed()")
        if (! mRewardedVideoAd.isLoaded) {
            loadRewardedVideoAd()
        }
    }

    override fun onRewarded(rewardItem: RewardItem) {
        Log.d("test", "onRewarded()")

    }

    override fun onRewardedVideoAdLeftApplication() {
        Log.d("test", "onRewardedVideoAdLeftApplication()")

    }

    override fun onRewardedVideoAdFailedToLoad(errorCode: Int) {
        Log.d("test", "onRewardedVideoAdFailedToLoad() - $errorCode")
        progressLayout.hide()
        if (errorCode == 0){
            loadRewardedVideoAd()
        }
    }

    override fun onRewardedVideoCompleted() {
        /*mLockService = new CheckForLoqService(getApplicationContext());
        if (isMyServiceRunning(mLockService.getClass()))
            stopService(new Intent(getApplicationContext(), mLockService.getClass()));

        mLockService.allowUsage(60000);
        startService(new Intent(getApplicationContext(), mLockService.getClass()));*/
        //setVisible(false)
       // Utils.INSTANCE.createPauseFile(applicationContext) todo: 7/25/19 handle whatever this is for
        //finish()
        Log.d("test", "onRewardedVideoCompleted")
    }

    private var serviceConnection:ServiceConnection? = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            inAppBillingService = null
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            inAppBillingService = IInAppBillingService.Stub.asInterface(service)
        }
    }

    override fun onPause() {
        super.onPause()
        mRewardedVideoAd.pause(this)
    }

    override fun onResume() {
        super.onResume()
        mRewardedVideoAd.resume(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mRewardedVideoAd.destroy(this)
     /*   unbindService(serviceConnection)
        serviceConnection = null*/
    }

    companion object {

        private val UNLOCK_PURCHASE_PRODUCT = "unloq_time"
    }
}
