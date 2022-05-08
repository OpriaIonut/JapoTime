package com.example.japotimeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.japotimeapp.fragments.MainPageFragment;

public class MainActivity extends AppCompatActivity {

    public static final String inputUrl = "https://japotime.fra1.digitaloceanspaces.com/Yomichan.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, new MainPageFragment(this)).commit();
    }
}