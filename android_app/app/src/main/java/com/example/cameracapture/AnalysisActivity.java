package com.example.cameracapture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class AnalysisActivity extends AppCompatActivity {
    private Button homeButton;
    private TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        // initialize buttons
        initializeComponents();

        // getting the intent and the content
        Intent analysedIntent = getIntent();

        // getting intent values
        String resultString = analysedIntent.getStringExtra("response_key");

        // setting it to the result view
        resultView.setText(resultString);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToHome();
            }
        });
    }

    // PRIVATE METHODS:

    // method: go to main page
    private void switchToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Initializing buttons
    private void initializeComponents() {
        homeButton = findViewById(R.id.analysisHomeButton);
        resultView = findViewById(R.id.resultTextView);
    }
}