package com.loq.buggadooli.loq2.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.loq.buggadooli.loq2.R;

public class CongratsActivity extends AppCompatActivity {

    private Button btnDashboard;
    private Button btnTwitterShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congrats);
        getSupportActionBar().hide();
        btnDashboard = findViewById(R.id.btnDashboard);
        btnDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchDashboard();
            }
        });
        btnTwitterShare = findViewById(R.id.btnTwitterShare);
        btnTwitterShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareOnTwitter();
            }
        });
    }

    private void launchDashboard() {
        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
        startActivity(intent);
    }

    private void shareOnTwitter() {
        Intent tweet = new Intent(Intent.ACTION_VIEW);
        tweet.setData(Uri.parse("http://twitter.com/?status=" + Uri.encode("I am now living #LifeWithLoq on Android!")));//where message is your string message
        startActivity(tweet);
    }
}
