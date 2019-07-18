package com.loq.buggadooli.loq2.ui.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.loq.buggadooli.loq2.R;

public class SetupActivity extends AppCompatActivity {

    private Spinner popularAppsSpinner;
    private RadioGroup appSelectGroup;
    private Button btnNext;
    private boolean chooseApps = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        getSupportActionBar().hide();
        popularAppsSpinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.popular_apps, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        popularAppsSpinner.setAdapter(adapter);
        appSelectGroup = findViewById(R.id.appSelectGroup);
        appSelectGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioBtnPopular) {
                    chooseApps = false;
                    popularAppsSpinner.setVisibility(View.VISIBLE);
                } else if(checkedId == R.id.radioBtnCustom) {
                    chooseApps = true;
                    popularAppsSpinner.setVisibility(View.INVISIBLE);
                }
            }
        });

        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LockActivity.class);
                intent.putExtra("chooseApps", chooseApps);
                startActivity(intent);
            }
        });
    }

}
