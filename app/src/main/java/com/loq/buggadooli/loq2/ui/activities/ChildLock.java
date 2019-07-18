package com.loq.buggadooli.loq2.ui.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loq.buggadooli.loq2.R;
import com.loq.buggadooli.loq2.utils.Utils;

public class ChildLock extends AppCompatActivity {

    private TextView txtPinCode;
    private TextView txtPinCode2;
    private Button btnEnter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_lock);
        getSupportActionBar().hide();
        txtPinCode = findViewById(R.id.txtPinCode);
        txtPinCode2 = findViewById(R.id.txtPinCode2);
        btnEnter = findViewById(R.id.btnEnter);
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChildLock();
            }
        });
    }

    private void saveChildLock() {
        String pinCode = txtPinCode.getText().toString();
        String pinCode2 = txtPinCode2.getText().toString();
        if(pinCode.equals(pinCode2)) {
            if(!pinCode.isEmpty()) {
                Utils.INSTANCE.saveChildLoqPin(getApplicationContext(), pinCode);
            }
            Intent intent = new Intent(getApplicationContext(), CongratsActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Pin codes do not match!",
                    Toast.LENGTH_LONG).show();
        }
    }
}
