package com.loq.buggadooli.loq2.ui.activities;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.loq.buggadooli.loq2.R;
import com.loq.buggadooli.loq2.utils.Utils;

public class MainActivity extends AppCompatActivity {

    //private RecyclerView appSelector;
    //private SelectionTracker<Long> tracker = null;
    //private AppSelectionAdapter adapter = null;
    //private Button btnRegister;
    //private Button btnSignin;
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        progressBar = findViewById(R.id.progressBar);
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                launchActivity();
            }
        }).start();
    }

    private void launchActivity() {
        if(Utils.INSTANCE.readLoqsFromFile(getApplicationContext()).isEmpty()) {
            launchRegistrationActivity();
        } else {
            launchDashboard();
        }
    }

    private void launchDashboard() {
        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
        startActivity(intent);
    }

    private void launchRegistrationActivity() {
        Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
        startActivity(intent);
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
