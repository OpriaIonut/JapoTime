package com.example.japotimeapp.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.HashMap;
import java.util.List;

public class UserData
{
    public List<KanjiCard> kanjiCards;
    public String lastOpenDate;

    public List<Integer> newCardsIDs;
    public List<Integer> refreshCardsIDs;
    public List<Integer> inReviewIDs;
    public List<Integer> lastCheckIDs;

    public int newCardsLimit = 20;
    public int refreshCardsLimit = 40;
    public int inReviewLimit = 10;

    public HashMap<String, String> studiedTimeHistory;
    public HashMap<String, Integer> studiedCardsHistory;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public UserData(List<KanjiCard> _kanjiCards, DailyReview dailyReview, String currentDate, HashMap<String, String> studyTime, HashMap<String, Integer> cardsStudied)
    {
        lastOpenDate = currentDate;

        studiedTimeHistory = studyTime;
        if(studiedTimeHistory == null)
            studiedTimeHistory = new HashMap<>();
        studiedTimeHistory.put(currentDate, dailyReview.totalSpentTimeStudying);

        studiedCardsHistory = cardsStudied;
        if(studiedCardsHistory == null)
            studiedCardsHistory = new HashMap<>();
        studiedCardsHistory.put(currentDate, dailyReview.cardsStudiedToday);

        newCardsIDs = dailyReview.newCardsIDs;
        refreshCardsIDs = dailyReview.refreshCardsIDs;
        inReviewIDs = dailyReview.inReviewIDs;
        lastCheckIDs = dailyReview.lastCheckIDs;

        newCardsLimit = dailyReview.newCardsLimit;
        refreshCardsLimit = dailyReview.refreshCardsLimit;
        inReviewLimit = dailyReview.inReviewLimit;

        kanjiCards = _kanjiCards;
    }
}
