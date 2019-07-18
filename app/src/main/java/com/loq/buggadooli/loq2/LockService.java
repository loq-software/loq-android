package com.loq.buggadooli.loq2;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.loq.buggadooli.loq2.models.Loq;
import com.loq.buggadooli.loq2.ui.activities.LockScreenActivity;
import com.loq.buggadooli.loq2.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LockService extends Service {

    private List<Loq> loqs = new ArrayList<>();
    private SimpleDateFormat sfd =  new SimpleDateFormat("HH:mm");
    private boolean allowUsage = false;
    private Intent dialogIntent = null;
    public LockService() {

    }

    public LockService(Context applicationContext) {
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        scheduleMethod();
        showNotification();
        return START_STICKY;
    }

    private void scheduleMethod() {
        ScheduledExecutorService scheduler = Executors
                .newSingleThreadScheduledExecutor();
        final boolean[] launched = {false};
        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                // This method will check for the Running apps after every 100ms
                //if(30 minutes spent){
                    //stop();
                //}else{
                if(launched[0] == true) return;
                launched[0] = true;
                loqs = Utils.INSTANCE.readLoqsFromFile(getApplicationContext());
                if(Utils.INSTANCE.shouldPause(getApplicationContext()))
                    allowUsage(60000);

                checkRunningApps();
                //}
            }
        }, 1000, 3000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent(this, LockServiceBroadcastReceiver.class);

        sendBroadcast(broadcastIntent);
    }

    private void checkRunningApps() {

        if(allowUsage)
            return;

        String activityOnTop = getForegroundApp();

        // Provide the packagename(s) of apps here, you want to show password activity
        if (isAppLocked(activityOnTop) && dialogIntent==null) { // you can make this check even better {
            dialogIntent = new Intent(this, LockScreenActivity.class);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(dialogIntent);
        } else {
            dialogIntent = null;
        }
    }

    private boolean isAppLocked(String app) {
        for(Loq loq : loqs) {

            if(loq.appName.equalsIgnoreCase(app) || loq.packageName.equalsIgnoreCase(app)) {
                if(isLockTime(loq))
                    return true;
            }
        }
        return false;
    }

    private boolean isLockTime(Loq loq) {
        Date date = new Date();   // given date
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int startHour = Integer.parseInt(loq.rawStartHour);
        int endHour = Integer.parseInt(loq.rawEndHour);
        int startMintute = Integer.parseInt(loq.rawStartMinute);
        int endMinute = Integer.parseInt(loq.rawEndMinute);
        if(hour >= startHour && hour <= endHour) {
            if(hour == startHour && minute < startMintute)
                return false;
            if(hour == endHour && minute > endMinute)
                return false;
            return true;
        }
        return false;
    }

    public String getForegroundApp() {
        String currentApp = "NULL";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }

        return currentApp;
    }

    private void allowUsage(long timeInMillis) {
        allowUsage = true;
        new CountDownTimer(timeInMillis, 10000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                allowUsage = false;
            }
        }.start();
    }

    private void showNotification() {

        String channelId = "default channel";

        Notification notification = new NotificationCompat.Builder(this,channelId)
                .setContentTitle(/*resources.getString( R.string.app_name )*/"test")
                .setContentText("Location Service")
                //.setSmallIcon(R.drawable.icon_transparent)
                .setOngoing(true)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "ForegroundChannel",
                    NotificationManager.IMPORTANCE_LOW);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.createNotificationChannel(channel);
        }

        startForeground(1, notification);

    }
}
