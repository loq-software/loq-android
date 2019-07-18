package com.loq.buggadooli.loq2.ui.activities;

import android.app.TimePickerDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;

import com.loq.buggadooli.loq2.models.Loq;
import com.loq.buggadooli.loq2.R;
import com.loq.buggadooli.loq2.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditLoq extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private List<CheckBox> days = new ArrayList<>();
    private TextView txtAppName;
    private Button btnStartTime;
    private Button btnEndTime;
    private Button btnSaveChanges;
    private Loq thisLoq;
    private int loqIndex;
    private boolean isStartTime = false;
    private String rawStartHour;
    private String rawStartMinute;
    private String rawEndHour;
    private String rawEndMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_loq);
        loqIndex = getIntent().getIntExtra("loqIndex", 0);
        getSupportActionBar().hide();
        getDayCheckboxes();
        txtAppName = findViewById(R.id.txtAppName);
        btnStartTime = findViewById(R.id.btnStartTime);
        btnStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartTime = true;
                showTimePicker();
            }
        });
        btnEndTime = findViewById(R.id.btnEndTime);
        btnEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartTime = false;
                showTimePicker();
            }
        });
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        initLoq();
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEditedLoqs();
                finish();
            }
        });
    }

    private void saveEditedLoqs() {
        thisLoq.startTime = btnStartTime.getText().toString();
        thisLoq.rawStartHour = rawStartHour;
        thisLoq.rawStartMinute = rawStartMinute;
        thisLoq.rawEndHour = rawEndHour;
        thisLoq.rawEndMinute = rawEndMinute;
        thisLoq.endTime = btnEndTime.getText().toString();
        List<String> selectedDays = getSelectedDays();
        if(!selectedDays.isEmpty()) {
            thisLoq.Days = getSelectedDays();
            String days = "";
            for (String day : thisLoq.Days) {
                days += day + " ";
            }
            thisLoq.daysStr = days;
        }
        List<Loq> loqs = Utils.INSTANCE.getCurrentLoqs();
        loqs.remove(loqIndex);
        loqs.add(loqIndex, thisLoq);
        Utils.INSTANCE.setCurrentLoqs(loqs);
        String json = Utils.INSTANCE.convertLoqsToJson(loqs);
        Utils.INSTANCE.saveLoqsToFile(getApplicationContext(), json);
    }

    private void initLoq() {
        thisLoq = Utils.INSTANCE.getEditLoq();
        String day;
        if(thisLoq.Days != null) {
            for (CheckBox chkDay : days) {
                day = chkDay.getText().toString();
                if (thisLoq.Days.contains(day)) {
                    chkDay.setChecked(true);
                }
            }
        }

        btnStartTime.setText(thisLoq.startTime);
        btnEndTime.setText(thisLoq.endTime);
        txtAppName.setText(thisLoq.appName);
    }

    private void showTimePicker() {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(EditLoq.this, this, hour, minute,
                DateFormat.is24HourFormat(this));
        dialog.show();
    }

    private void getDayCheckboxes() {
        days.add((CheckBox) findViewById(R.id.chkSunday));
        days.add((CheckBox) findViewById(R.id.chkMonday));
        days.add((CheckBox) findViewById(R.id.chkTuesday));
        days.add((CheckBox) findViewById(R.id.chkWednesday));
        days.add((CheckBox) findViewById(R.id.chkThursday));
        days.add((CheckBox) findViewById(R.id.chkFriday));
        days.add((CheckBox) findViewById(R.id.chkSaturday));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        int rawHour = hourOfDay;
        int rawMinute = minute;
        String timeOfDay = "AM";
        if(hourOfDay > 11) {
            timeOfDay = "PM";
        }

        if(hourOfDay > 12) {
            hourOfDay -= 12;
        }

        String hourStr = String.valueOf(hourOfDay);
        String minuteStr = String.valueOf(minute);
        if(isStartTime) {
            rawStartHour = String.valueOf(rawHour);
            rawStartMinute = String.valueOf(rawMinute);
            btnStartTime.setText(hourStr + ":" + minuteStr + " " + timeOfDay);
        } else {
            rawEndHour = String.valueOf(rawHour);
            rawEndMinute = String.valueOf(rawMinute);
            btnEndTime.setText(hourStr + ":" + minuteStr + " " + timeOfDay);
        }
    }

    private List<String> getSelectedDays() {
        List<String> selectedDays = new ArrayList<>();
        for (CheckBox item : days){
            if(item.isChecked())
                selectedDays.add(item.getText().toString());
        }
        return selectedDays;
    }
}
