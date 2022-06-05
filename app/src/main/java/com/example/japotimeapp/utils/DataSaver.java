package com.example.japotimeapp.utils;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amplifyframework.core.Amplify;
import com.example.japotimeapp.MainActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.security.cert.Extension;
import java.util.HashMap;
import java.util.Scanner;

public class DataSaver
{
    private final SharedPreferences sharedPreferences;
    private final MainActivity mainActivity;

    private String currentOnlineSaveFile = "";

    public UserData loadedData = null;

    public DataSaver(SharedPreferences _shared, MainActivity _mainActivity)
    {
        sharedPreferences = _shared;
        mainActivity = _mainActivity;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void SaveData(String currentDate, Boolean resetEverything)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        HashMap<String, String> studyTimeHistory = null;
        if(loadedData != null)
            studyTimeHistory = loadedData.studiedTimeHistory;

        HashMap<String, Integer> studiedCardsHistory = null;
        if(loadedData != null)
            studiedCardsHistory = loadedData.studiedCardsHistory;

        if(resetEverything)
        {
            studyTimeHistory = new HashMap<>();
            studiedCardsHistory = new HashMap<>();
        }

        UserData userData = new UserData(mainActivity.kanjiCollection.cardsCollection, mainActivity.dailyReview, currentDate, studyTimeHistory, studiedCardsHistory);
        userData.onlineSaveDataFile = currentOnlineSaveFile;
        String json = gson.toJson(userData);
        editor.putString("userProgressData", json);
        editor.apply();
    }

    public void LoadData()
    {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("userProgressData", null);
        Type type = new TypeToken<UserData>(){}.getType();
        loadedData = gson.fromJson(json, type);

        currentOnlineSaveFile = loadedData.onlineSaveDataFile;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void SaveDataOnline(String fileName, String currentDate) {
        currentOnlineSaveFile = fileName;

        Gson gson = new Gson();
        HashMap<String, String> studyTimeHistory = null;
        if (loadedData != null)
            studyTimeHistory = loadedData.studiedTimeHistory;

        HashMap<String, Integer> studiedCardsHistory = null;
        if (loadedData != null)
            studiedCardsHistory = loadedData.studiedCardsHistory;

        UserData newUserData = new UserData(mainActivity.kanjiCollection.cardsCollection, mainActivity.dailyReview, currentDate, studyTimeHistory, studiedCardsHistory);
        String json = gson.toJson(newUserData);

        InputStream stream = new ByteArrayInputStream(json.getBytes());

        Amplify.Storage.uploadInputStream("JapoTimeFiles/UserData/" + fileName + ".json", stream,
                result -> Toast.makeText(mainActivity, "File uploaded successfully", Toast.LENGTH_SHORT).show(),
                error -> Log.e("JapoTimeApp", "Upload failed", error)
        );
    }

    public void LoadDataOnline(String fileName, String currentDate)
    {
        currentOnlineSaveFile = fileName;

        File downloadedFile = new File(mainActivity.getFilesDir() + "/userProgress.txt");

        Amplify.Storage.downloadFile("JapoTimeFiles/UserData/" + fileName + ".json", downloadedFile,
                result -> {
                    Toast.makeText(mainActivity, "File downloaded successfully", Toast.LENGTH_SHORT).show();

                    String data = "";
                    try
                    {
                        Scanner myReader = new Scanner(downloadedFile);
                        while (myReader.hasNextLine())
                        {
                            data = myReader.nextLine();
                        }
                        myReader.close();
                    }
                    catch (FileNotFoundException e)
                    {
                        System.out.println("An error occurred.");
                        e.printStackTrace();
                    }

                    Gson gson = new Gson();
                    Type type = new TypeToken<UserData>(){}.getType();
                    loadedData = gson.fromJson(data, type);

                    mainActivity.kanjiCollection.cardsCollection = loadedData.kanjiCards;

                    mainActivity.dailyReview.newCardsIDs = loadedData.newCardsIDs;
                    mainActivity.dailyReview.refreshCardsIDs = loadedData.refreshCardsIDs;
                    mainActivity.dailyReview.inReviewIDs = loadedData.inReviewIDs;
                    mainActivity.dailyReview.lastCheckIDs = loadedData.lastCheckIDs;

                    mainActivity.dailyReview.newCardsLimit = loadedData.newCardsLimit;
                    mainActivity.dailyReview.refreshCardsLimit = loadedData.refreshCardsLimit;
                    mainActivity.dailyReview.inReviewLimit = loadedData.inReviewLimit;

                    if(loadedData.studiedCardsHistory.containsKey(currentDate))
                        mainActivity.dailyReview.cardsStudiedToday = loadedData.studiedCardsHistory.get(currentDate);
                    else
                        mainActivity.dailyReview.cardsStudiedToday = 0;

                    if(loadedData.studiedTimeHistory.containsKey(currentDate))
                        mainActivity.dailyReview.totalSpentTimeStudying = loadedData.studiedTimeHistory.get(currentDate);
                    else
                        mainActivity.dailyReview.totalSpentTimeStudying = "00:00:00";
                },
                error -> Toast.makeText(mainActivity, "File not found", Toast.LENGTH_SHORT).show()
        );
    }

    public void ClearSharedPrefs(){
        sharedPreferences.edit().remove("userProgressData").commit();
    }
}
