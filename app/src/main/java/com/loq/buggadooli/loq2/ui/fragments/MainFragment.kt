package com.loq.buggadooli.loq2.ui.fragments

import android.os.Handler
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.loq.buggadooli.loq2.R
import com.loq.buggadooli.loq2.extensions.inflateTo
import com.loq.buggadooli.loq2.extensions.replaceFragment
import com.loq.buggadooli.loq2.extensions.safeActivity
import com.loq.buggadooli.loq2.ui.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.main_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MainFragment : Fragment() {

    private var progressStatus = 0
    private val handler = Handler()
    private val mainViewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflateTo(R.layout.main_fragment, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            updateUi()
        }).start()
    }

    private fun updateUi() {
        if (mainViewModel.user == null) {
            launchLogin()
        } else {
            safeActivity.replaceFragment(fragment = DashboardFragment())
        }
    }

    private fun launchLogin() {
        safeActivity.replaceFragment( fragment = LoginFragment())
    }

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
