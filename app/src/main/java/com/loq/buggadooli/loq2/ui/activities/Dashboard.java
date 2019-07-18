package com.loq.buggadooli.loq2.ui.activities;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.loq.buggadooli.loq2.LockService;
import com.loq.buggadooli.loq2.models.Loq;
import com.loq.buggadooli.loq2.R;
import com.loq.buggadooli.loq2.utils.Utils;
import com.loq.buggadooli.loq2.ui.adapters.LoqSelectionAdapter;
import com.loq.buggadooli.loq2.ui.listeners.LoqSelectionEditListener;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;

public class Dashboard extends AppCompatActivity {

    private RecyclerView loqList;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter loqAdapter;
    private boolean initialized = false;
    private TextView txtPin;
    private Button btnUnlock;
    private View lockBlock;
    private String lockPin;
    private LockService mLockService;
    private Intent mServiceIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().hide();
        loqList = findViewById(R.id.listCurrentLoqs);
        txtPin = findViewById(R.id.txtPin);
        btnUnlock = findViewById(R.id.btnUnlock);
        lockBlock = findViewById(R.id.childLock);
        initLockBlock();
        initLoqList();
        startLockService();
    }

    private void initLockBlock() {
        lockPin = Utils.INSTANCE.getChildLoqPin(getApplicationContext());
        if(!lockPin.isEmpty()) {
            lockBlock.setVisibility(View.VISIBLE);
        }
        btnUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtPin.getText().toString().equals(lockPin)) {
                    lockBlock.setVisibility(View.GONE);
                }

            }
        });
    }

    private void startLockService() {
        mLockService = new LockService(getApplicationContext());
        if (isMyServiceRunning(mLockService.getClass())) {
            stopService(new Intent(getApplicationContext(), mLockService.getClass()));
            startService(new Intent(getApplicationContext(), mLockService.getClass()));
        } else {
            mServiceIntent = new Intent(getApplicationContext(), mLockService.getClass());
            PendingIntent pintent = PendingIntent.getService(this, 0, mServiceIntent, 0);
            AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), 1000, pintent);
        }
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

    private void initLoqList() {
        List<Loq> currentLoqs = Utils.INSTANCE.readLoqsFromFile(this);
        if(!currentLoqs.isEmpty()) {
            layoutManager = new LinearLayoutManager(getApplicationContext());
            loqList.setLayoutManager(layoutManager);
            loqAdapter = new LoqSelectionAdapter(currentLoqs);
            ((LoqSelectionAdapter) loqAdapter).setListener(new LoqSelectionEditListener() {
                @Override
                public void onLoqSelectionEditListenerClicked(@NotNull Loq loq, int index) {
                    Utils.INSTANCE.setEditLoq(loq);
                    Intent intent = new Intent(Dashboard.this, EditLoq.class);
                    intent.putExtra("loqIndex", index);
                    startActivity(intent);
                }
            });
            loqList.setAdapter(loqAdapter);
        }
        initialized = true;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(initialized) {
            List<Loq> currentLoqs = Utils.INSTANCE.getCurrentLoqs();
            layoutManager = new LinearLayoutManager(getApplicationContext());
            loqList.setLayoutManager(layoutManager);
            loqAdapter = new LoqSelectionAdapter(currentLoqs);
            loqList.setAdapter(loqAdapter);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity(); // or finish();
    }
}
