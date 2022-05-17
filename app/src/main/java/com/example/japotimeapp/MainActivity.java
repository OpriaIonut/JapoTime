package com.example.japotimeapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.example.japotimeapp.fragments.LoadingScreenFragment;
import com.example.japotimeapp.fragments.MainPageFragment;
import com.example.japotimeapp.utils.DailyReview;
import com.example.japotimeapp.utils.DataSaver;
import com.example.japotimeapp.utils.KanjiCard;
import com.example.japotimeapp.utils.KanjiCollection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String inputUrl = "https://japotime.fra1.digitaloceanspaces.com/Yomichan.txt";

    public KanjiCollection kanjiCollection = null;
    public DailyReview dailyReview = null;
    public DataSaver dataSaver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, new LoadingScreenFragment(this)).commit();
        new FetchData().start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onConfigFileLoaded(List<String> result)
    {
        dataSaver = new DataSaver(getSharedPreferences("shared preferences", MODE_PRIVATE), this);
        dataSaver.LoadData();

        kanjiCollection = new KanjiCollection(result, dataSaver);
        dailyReview = new DailyReview(dataSaver, kanjiCollection);

        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, new MainPageFragment(this)).commit();
    }

    public class FetchData extends Thread
    {
        List<String> data = new ArrayList<>();

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run()
        {
            try
            {
                URL url = new URL(inputUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader((new InputStreamReader(inputStream)));
                String line;

                while((line = bufferedReader.readLine()) != null)
                {
                    data.add(line);
                }
                onConfigFileLoaded(data);
            }
            catch(MalformedURLException e) {
                e.printStackTrace();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}