package com.app.covid19;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AdvicesActivity extends AppCompatActivity {

    CardView cardGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advices);

        cardGetStarted = findViewById(R.id.getStarted);

        cardGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdvicesActivity.this, MainActivity.class));
                finish();
            }
        });

    }
}