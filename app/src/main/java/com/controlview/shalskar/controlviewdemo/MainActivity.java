package com.controlview.shalskar.controlviewdemo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.controlview.shalskar.controlview.ControlView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ControlView controlView1;
    private ControlView controlView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.controlView1 = (ControlView) findViewById(R.id.controlview1);
        this.controlView1.setBaseColour(android.R.color.white);
        this.controlView1.setAccentColour(R.color.colorAccent);
        this.controlView1.setControlOptions(Arrays.asList("Auction", "Classified", "Other"));
        this.controlView1.setOnControlOptionSelectedListener(new ControlView.OnControlOptionSelectedListener() {
            @Override
            public void onControlOptionSelected(int position, @NonNull String controlOption) {
                Toast.makeText(MainActivity.this, controlOption, Toast.LENGTH_SHORT).show();
            }
        });

        this.controlView2 = (ControlView) findViewById(R.id.controlview2);
        this.controlView2.setControlOptions(Arrays.asList("Hi", "Bye", "No", "I concur", "T"));
        this.controlView2.setOnControlOptionSelectedListener(new ControlView.OnControlOptionSelectedListener() {
            @Override
            public void onControlOptionSelected(int position, @NonNull String controlOption) {
                Toast.makeText(MainActivity.this, controlOption, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
