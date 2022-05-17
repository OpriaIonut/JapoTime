package com.example.japotimeapp.utils;

import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.japotimeapp.MainActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class DataSaver
{
    private final SharedPreferences sharedPreferences;
    private final MainActivity mainActivity;

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
    }

    public void ClearSharedPrefs(){
        sharedPreferences.edit().remove("userProgressData").commit();
    }
}
