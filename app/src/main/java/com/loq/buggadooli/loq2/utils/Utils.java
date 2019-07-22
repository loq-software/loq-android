package com.loq.buggadooli.loq2.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.loq.buggadooli.loq2.models.Loq;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Utils {
    INSTANCE;

    public static String APP_NAME = "Loq";
    private List<ApplicationInfo> appInfos = new ArrayList<>();
    private List<String> packageNames = new ArrayList<>();
    private Loq editLoq;
    private List<Loq> currentLoqs;

    public void addAppInfo(ApplicationInfo appInfo) {
        appInfos.add(appInfo);
        packageNames.add(appInfo.packageName);
    }

    public void saveLoqsToFile(Context context, String json) {
        File file = new File(context.getFilesDir(), "saved_loqs");
        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(json);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createPauseFile(Context context) {
        File file = new File(context.getFilesDir(), "pause_file");
        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void saveChildLoqPin(Context context, String pin) {
        File file = new File(context.getFilesDir(), "child_lock");
        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(pin);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean shouldPause(Context context) {
        File file = new File(context.getFilesDir(), "pause_file");
        if(file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }

    public String getChildLoqPin(Context context) {
        String lockPin;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = context.openFileInput("child_lock");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            lockPin = bufferedReader.readLine();
        } catch (IOException e) {
            return "";
        }
        return lockPin;
    }

    public List<Loq> readLoqsFromFile(Context context) {
        List<Loq> loqs = new ArrayList<>();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = context.openFileInput("saved_loqs");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String jsonData = bufferedReader.readLine();
            bufferedReader.close();
            inputStreamReader.close();
            JSONObject jsonObj = new JSONObject(jsonData);
            JSONArray loqData = jsonObj.getJSONArray("Loqs");
            for (int i = 0; i < loqData.length(); i++) {
                JSONObject loqObj = loqData.getJSONObject(i);
                Loq lock = new Loq();
                lock.appName = loqObj.getString("AppName");
                lock.daysStr = loqObj.getString("days");
                lock.days = Arrays.asList(lock.daysStr.split(" "));
                lock.startTime = loqObj.getString("StartTime");
                lock.endTime = loqObj.getString("EndTime");
                lock.packageName = loqObj.getString("PackageName");
                lock.rawStartHour = loqObj.getString("StartHour");
                lock.rawStartMinute = loqObj.getString("StartMinute");
                lock.rawEndHour = loqObj.getString("EndHour");
                lock.rawEndMinute = loqObj.getString("EndMinute");
                loqs.add(lock);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        currentLoqs = loqs;
        return loqs;
    }

    public void setEditLoq(Loq loq) {
        editLoq = loq;
    }

    public Loq getEditLoq() {
        return editLoq;
    }

    public void setCurrentLoqs(List<Loq> loqs) {
        currentLoqs = loqs;
    }

    public List<Loq> getCurrentLoqs() {
        return currentLoqs;
    }

    public String convertLoqsToJson(List<Loq> newLoqs) {
        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArr = new JSONArray();
        for(Loq loq : newLoqs) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("AppName", loq.appName);
                obj.put("days", loq.daysStr);
                obj.put("PackageName", loq.packageName);
                obj.put("StartTime", loq.startTime);
                obj.put("EndTime", loq.endTime);
                obj.put("EndHour", loq.rawEndHour);
                obj.put("EndMinute", loq.rawEndMinute);
                obj.put("StartHour", loq.rawStartHour);
                obj.put("StartMinute", loq.rawStartMinute);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArr.put(obj);
        }

        try {
            jsonObj.put("Loqs", jsonArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObj.toString();
    }

    @Nullable
    public static CharSequence makeLoqItemTimeString(@NotNull String startTime, @NotNull String endTime) {
        return startTime + " - " + endTime;
    }
}
