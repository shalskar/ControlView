package com.controlview.shalskar.controlviewdemo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.controlview.shalskar.controlview.ControlView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_IMAGE_URL = "http://lorempixel.com/1920/1080/";
    private static final String[] ITEMS = {"Sports", "City", "Cats"};

    private ControlView controlView;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseViews();
    }

    private void initialiseViews() {
        initialiseControlView();
        initialiseImageView();
    }

    private void initialiseControlView() {
        this.controlView = (ControlView) findViewById(R.id.controlview);
        this.controlView.setBaseColour(android.R.color.white);
        this.controlView.setAccentColour(R.color.colorAccent);
        this.controlView.setControlOptions(new ArrayList<>(Arrays.asList(ITEMS)));
        this.controlView.setOnControlOptionSelectedListener(new ControlView.OnControlOptionSelectedListener() {
            @Override
            public void onControlOptionSelected(int position, @NonNull String controlOption) {
                loadImage(position);
            }
        });
    }

    private void initialiseImageView() {
        this.imageView = (ImageView) findViewById(R.id.imageview);
        loadImage(0);
    }

    private void loadImage(int position) {
        String url = BASE_IMAGE_URL + ITEMS[position].toLowerCase();
        Picasso.with(this)
                .load(url)
                .centerCrop()
                .fit()
                .into(this.imageView);
    }
}
