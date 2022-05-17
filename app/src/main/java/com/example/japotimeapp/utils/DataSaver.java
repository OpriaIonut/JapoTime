package com.example.japotimeapp.utils;

import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.japotimeapp.MainActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class DataSaver
{
    private SharedPreferences sharedPreferences;
    private MainActivity mainActivity;

    public UserData loadedData;

    public DataSaver(SharedPreferences _shared, MainActivity _mainActivity)
    {
        sharedPreferences = _shared;
        mainActivity = _mainActivity;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void SaveData()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        UserData userData = new UserData(mainActivity.kanjiCollection.cardsCollection);
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
