package com.reversi;

import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    ReversiView reversiView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reversiView = findViewById(R.id.board);

        Log.d("Size of surface view", "Value: " + reversiView.getWidth());

    }
}