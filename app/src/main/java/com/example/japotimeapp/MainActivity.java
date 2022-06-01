package com.example.japotimeapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.example.japotimeapp.enums.ActiveFragment;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.SimpleFormatter;

public class MainActivity extends AppCompatActivity {

    public static final String inputUrl = "https://japotime.fra1.digitaloceanspaces.com/Yomichan.txt";

    public ActiveFragment currentActiveFragment;

    public KanjiCollection kanjiCollection = null;
    public DailyReview dailyReview = null;
    public DataSaver dataSaver = null;
    public String currentDate;

    public Boolean isStudyPageActive = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Date date = new Date();
        currentDate = new SimpleDateFormat("dd-MM-yyyy").format(date);

        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, new LoadingScreenFragment(this)).commit();
        currentActiveFragment = ActiveFragment.LoadingScreen;
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
        currentActiveFragment = ActiveFragment.MainPage;
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

    //Will be called when the app resumes, or control is given back to this activity (when the popup activities call finish())
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();

        if(currentActiveFragment == ActiveFragment.StudyPage)
        {
            dailyReview.studyStartTime = LocalTime.now();
        }
    }

    //When the app pauses or closes, save the data
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onPause() {
        super.onPause();

        if(currentActiveFragment == ActiveFragment.StudyPage)
            dailyReview.CalculateTimePassed();

        dataSaver.SaveData(currentDate, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStop() {
        super.onStop();
        dataSaver.SaveData(currentDate, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataSaver.SaveData(currentDate, false);
    }
}