package com.loq.buggadooli.loq2.ui.activities

import android.content.Intent
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar

import com.loq.buggadooli.loq2.R
import com.loq.buggadooli.loq2.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var progressBar: ProgressBar? = null
    private var progressStatus = 0
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()
        textView6
        progressBar = findViewById(R.id.progressBar)
        Thread(Runnable {
            while (progressStatus < 100) {
                progressStatus += 1
                // Update the progress bar and display the
                //current value in the text view
                handler.post { progressBar!!.progress = progressStatus }
                try {
                    Thread.sleep(50)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
            launchActivity()
        }).start()
    }

    private fun launchActivity() {
        // todo: Check if user is logged in instead.
        if (Utils.INSTANCE.readLoqsFromFile(applicationContext).isEmpty()) {
            launchRegistrationActivity()
        } else {
            launchDashboard()
        }
    }

    private fun launchDashboard() {
        val intent = Intent(applicationContext, Dashboard::class.java)
        startActivity(intent)
    }

    private fun launchRegistrationActivity() {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
    }

    /*private void startRegistration() {

    }*/

    /*private void initTracker(Bundle savedInstanceState) {
        tracker = new SelectionTracker.Builder<>(
                "activity-uri-selection",
                appSelector,
                new StableIdKeyProvider(appSelector),
                new AppSelectionLookup(appSelector),
                StorageStrategy.createLongStorage())
                .withSelectionPredicate(SelectionPredicates.<Long>createSelectAnything())
                .build();

        if(savedInstanceState != null)
            tracker.onRestoreInstanceState(savedInstanceState);

        addTrackerObserver();
        adapter.setTracker(tracker);
    }

    private void addTrackerObserver() {
        tracker.addObserver(new SelectionTracker.SelectionObserver<Long>() {
            @Override
            public void onSelectionChanged() {
                int nItems = tracker.getSelection().size();
                if(nItems > 0) {

                    // Change title and color of action bar

                    String title = Integer.toString(nItems) + " Items items selected";
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(
                            Color.parseColor("#ef6c00")));
                    setTitle(title);
                } else {
                   String  title = "Loq";
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorPrimary)));
                    setTitle(title);
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(outState != null)
            tracker.onSaveInstanceState(outState);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    public void lockBtnClicked(View view) {
        if(!isAccessGranted()) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }
        startService(new Intent(this, LockService.class));
    }*/
}
