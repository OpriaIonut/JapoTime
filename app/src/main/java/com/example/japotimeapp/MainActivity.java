package com.example.japotimeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;

import com.example.japotimeapp.fragments.MainPageFragment;
import com.example.japotimeapp.utils.KanjiCard;

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

    private List<KanjiCard> cardsCollection = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, new MainPageFragment(this)).commit();
        new FetchData().start();
    }

    public void onConfigFileLoaded(List<String> result)
    {
        for(int index = 0; index < result.size(); index++)
        {
            cardsCollection.add(new KanjiCard(result.get(index)));
        }
        System.out.println("Found: " + cardsCollection.size() + " cards.");
    }

    public class FetchData extends Thread
    {
        List<String> data = new ArrayList<>();

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