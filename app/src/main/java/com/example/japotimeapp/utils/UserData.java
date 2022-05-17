package com.example.japotimeapp.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class UserData
{
    public List<KanjiCard> kanjiCards;
    public LocalDate lastOpenDate;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public UserData(List<KanjiCard> _kanjiCards)
    {
        kanjiCards = _kanjiCards;
        lastOpenDate = null;//java.time.LocalDate.now();
    }
}
