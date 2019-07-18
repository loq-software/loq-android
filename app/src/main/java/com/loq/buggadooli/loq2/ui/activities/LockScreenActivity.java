package com.loq.buggadooli.loq2.ui.activities;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.loq.buggadooli.loq2.LockService;
import com.loq.buggadooli.loq2.R;
import com.loq.buggadooli.loq2.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class LockScreenActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private static String UNLOCK_PURCHASE_PRODUCT = "unloq_time";
    private RewardedVideoAd mRewardedVideoAd;
    private ImageView imgAd;
    private LockService mLockService;
    IInAppBillingService inAppBillingService;
    ServiceConnection serviceConnection;
    private Button btnPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        getSupportActionBar().hide();
        initAd();
        imgAd = findViewById(R.id.imgAd);
        imgAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRewardedVideoAd.isLoaded()) {
                    mRewardedVideoAd.show();
                    finish();
                }
            }
        });
        initBilling();
        btnPay = findViewById(R.id.btnPay);
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyItem(UNLOCK_PURCHASE_PRODUCT);
            }
        });
    }

    private void initBilling() {
        // Attach the service connection.
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                inAppBillingService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                inAppBillingService = IInAppBillingService.Stub.asInterface(service);
            }
        };

        // Bind the service.
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    private void buyItem(String productID) {
        if (inAppBillingService != null) {
            try {
                Bundle buyIntentBundle = inAppBillingService.getBuyIntent(3, getPackageName(), productID, "inapp", "bGoaFn4uQZbPiQJo4pf9RzJ+V7g/yqDXvKRqq+JT");
                PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                startIntentSenderForResult(pendingIntent.getIntentSender(), 1003, new Intent(), 0, 0, 0);
            } catch (RemoteException | IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1003 && resultCode == RESULT_OK) {

            final String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");

            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        JSONObject jo = new JSONObject(purchaseData);
                        // Get the productID of the purchased item.
                        String sku = jo.getString("productId");
                        if(sku.equals(UNLOCK_PURCHASE_PRODUCT)) {
                            inAppBillingService.consumePurchase(3, getPackageName(), jo.getString("purchaseToken"));
                            Toast.makeText(LockScreenActivity.this, "Unloq time" + " is successfully purchased. Enjoy!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LockScreenActivity.this, "Failed to make purchase.", Toast.LENGTH_LONG).show();
                        }
                    }catch (JSONException | RemoteException e) {
                        Toast.makeText(LockScreenActivity.this, "Failed to make purchase.", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }


    private void initAd() {
        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        loadRewardedVideoAd();
        mRewardedVideoAd.setRewardedVideoAdListener(this);
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {
        /*mLockService = new LockService(getApplicationContext());
        if (isMyServiceRunning(mLockService.getClass()))
            stopService(new Intent(getApplicationContext(), mLockService.getClass()));

        mLockService.allowUsage(60000);
        startService(new Intent(getApplicationContext(), mLockService.getClass()));*/
        setVisible(false);
        Utils.INSTANCE.createPauseFile(getApplicationContext());
        finish();
    }
}
