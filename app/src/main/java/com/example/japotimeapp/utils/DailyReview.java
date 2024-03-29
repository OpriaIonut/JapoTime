package com.example.japotimeapp.utils;

import android.os.Build;
import android.util.Pair;

import androidx.annotation.RequiresApi;

import com.example.japotimeapp.enums.CardAnswer;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class DailyReview
{
    private DataSaver dataSaver;
    private KanjiCollection kanjiCollection;

    public LocalTime studyStartTime;

    public List<Integer> newCardsIDs;
    public List<Integer> refreshCardsIDs;
    public List<Integer> inReviewIDs;
    public List<Integer> lastCheckIDs;

    public int newCardsLimit = 20;
    public int refreshCardsLimit = 40;
    public int inReviewLimit = 10;

    public int cardsStudiedToday = 0;
    public String totalSpentTimeStudying = "00:00:00";

    private String currentDate;
    private Random randomGenerator;

    private Boolean refreshCardsEmptied = false;
    public String pickedListForKanji = "";
    public int pickedListIndex = 0;

    private int reviewIterator = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public DailyReview(DataSaver _dataSaver, KanjiCollection _kanjiCollection)
    {
        dataSaver = _dataSaver;
        kanjiCollection = _kanjiCollection;

        Date date = new Date();
        currentDate = new SimpleDateFormat("dd-MM-yyyy").format(date);
        randomGenerator = new Random();

        if(dataSaver.loadedData == null)
        {
            newCardsIDs = new ArrayList<>();
            refreshCardsIDs = new ArrayList<>();
            inReviewIDs = new ArrayList<>();
            lastCheckIDs = new ArrayList<>();
        }
        else
        {
            newCardsIDs = dataSaver.loadedData.newCardsIDs;
            refreshCardsIDs = dataSaver.loadedData.refreshCardsIDs;
            inReviewIDs = dataSaver.loadedData.inReviewIDs;
            lastCheckIDs = dataSaver.loadedData.lastCheckIDs;

            refreshCardsLimit = dataSaver.loadedData.refreshCardsLimit;
            inReviewLimit = dataSaver.loadedData.inReviewLimit;
            newCardsLimit = dataSaver.loadedData.newCardsLimit;

            if(dataSaver.loadedData.studiedCardsHistory.get(currentDate) != null)
                cardsStudiedToday = dataSaver.loadedData.studiedCardsHistory.get(currentDate);

            if(dataSaver.loadedData.studiedTimeHistory.get(currentDate) != null)
                totalSpentTimeStudying = dataSaver.loadedData.studiedTimeHistory.get(currentDate);

            if(refreshCardsIDs.size() == 0)
                refreshCardsEmptied = true;
        }

        if((dataSaver.loadedData == null || dataSaver.loadedData.lastOpenDate == null || !dataSaver.loadedData.lastOpenDate.equals(currentDate) ) && newCardsIDs.size() == 0 && refreshCardsIDs.size() == 0 && inReviewIDs.size() == 0 && lastCheckIDs.size() == 0)
        {
            cardsStudiedToday = 0;
            GenerateDayReview();
        }
    }

    public int GetNewCardsCount() { return newCardsIDs.size(); }
    public int GetRefreshCardsCount() { return refreshCardsIDs.size(); }
    public int GetInReviewCardsCount() { return inReviewIDs.size(); }
    public int GetLastCheckCardsCount() { return lastCheckIDs.size(); }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void GenerateDayReview()
    {
        reviewIterator = 0;
        refreshCardsEmptied = false;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        //Find the kanji that should be reviewed
        List<Pair<Integer, Integer>> kanjiToReview = new ArrayList<>();
        for(int index = 0; index < kanjiCollection.cardsCollection.size(); index++)
        {
            KanjiCard currentCard = kanjiCollection.cardsCollection.get(index);

            if(currentCard.isCardDeleted)
                continue;

            //If it is a card that hasn't been learned, then add it to the list
            if(currentCard.masterScore == 0 && !newCardsIDs.contains(index))
            {
                if(newCardsIDs.size() < newCardsLimit)
                {
                    newCardsIDs.add(index);
                }
            }
            else if(currentCard.lastReviewDate != null && !refreshCardsIDs.contains(index) && !inReviewIDs.contains(index) && !lastCheckIDs.contains(index))
            {
                //Otherwise, if it has been learned, check if it should be learned now or not
                LocalDate lastReviewDate = LocalDate.parse(currentCard.lastReviewDate, formatter);
                LocalDate nextReviewDate = lastReviewDate.plusDays(currentCard.nextReviewDays);
                LocalDate dateNow = LocalDate.parse(currentDate, formatter);

                int reviewDayDifference = (int) ChronoUnit.DAYS.between(dateNow, nextReviewDate);
                if(reviewDayDifference <= 0)
                {
                    kanjiToReview.add(new Pair<>(index, reviewDayDifference));
                }
            }
        }

        //Sort priority cards in ascending order (the lower the second value of the pair is, the longer it has been since we last reviewed it).
        for(int index = 0; index < kanjiToReview.size() - 1; index++)
        {
            for(int index2 = index + 1; index2 < kanjiToReview.size(); index2++)
            {
                if(kanjiToReview.get(index).second > kanjiToReview.get(index2).second)
                {
                    Pair<Integer, Integer> aux = kanjiToReview.get(index);
                    kanjiToReview.set(index, kanjiToReview.get(index2));
                    kanjiToReview.set(index2, aux);
                }
            }
        }

        int cardsFound = Math.min(refreshCardsLimit - refreshCardsIDs.size(), kanjiToReview.size());
        for(int index = 0; index < cardsFound; index++)
        {
            refreshCardsIDs.add(kanjiToReview.get(index).first);
        }

        if(refreshCardsIDs.size() == 0)
            refreshCardsEmptied = true;
    }

    private Boolean clearLastCheck = false;

    public KanjiCard GetNextStudyCard()
    {
        //If we still have space for items in review
        if(inReviewIDs.size() < inReviewLimit && (newCardsIDs.size() > 0 || (newCardsIDs.size() == 0 && refreshCardsIDs.size() > 0)))
        {
            //Check to see if we reviewed all refresh items
            if(refreshCardsIDs.size() > 0)
            {
                //If not, then add those to the list
                int pickedIndex = randomGenerator.nextInt(refreshCardsIDs.size());
                pickedListForKanji = "Refresh";
                pickedListIndex = pickedIndex;
                return kanjiCollection.cardsCollection.get(refreshCardsIDs.get(pickedIndex));
            }
            else if(!refreshCardsEmptied)
            {
                if(inReviewIDs.size() > 0)
                {
                    //If we finished refresh cards, we need to empty review cards before moving on to new cards
                    if (reviewIterator >= inReviewIDs.size())
                        reviewIterator = 0;

                    int pickedIndex = reviewIterator;
                    pickedListForKanji = "Review";
                    pickedListIndex = pickedIndex;
                    reviewIterator++;

                    return kanjiCollection.cardsCollection.get(inReviewIDs.get(pickedIndex));
                }
                else
                {
                    refreshCardsEmptied = true;
                    //If we cleared refresh cards & review cards, start adding review cards
                    int pickedIndex = randomGenerator.nextInt(newCardsIDs.size());
                    pickedListForKanji = "New";
                    pickedListIndex = pickedIndex;

                    return kanjiCollection.cardsCollection.get(newCardsIDs.get(pickedIndex));
                }
            }
            else
            {
                //If we cleared refresh cards & review cards, start adding review cards
                int pickedIndex = randomGenerator.nextInt(newCardsIDs.size());
                pickedListForKanji = "New";
                pickedListIndex = pickedIndex;

                return kanjiCollection.cardsCollection.get(newCardsIDs.get(pickedIndex));
            }
        }
        else if(lastCheckIDs.size() > 0 || inReviewIDs.size() > 0)
        {
            if (!clearLastCheck)
            {
                if (reviewIterator >= inReviewIDs.size())
                    reviewIterator = 0;

                //If we reached review limit, add new cards
                int pickedIndex = reviewIterator;
                pickedListForKanji = "Review";
                pickedListIndex = pickedIndex;
                reviewIterator++;

                return kanjiCollection.cardsCollection.get(inReviewIDs.get(pickedIndex));
            }
            else
            {
                int pickedIndex = randomGenerator.nextInt(lastCheckIDs.size());
                pickedListForKanji = "LastCheck";
                pickedListIndex = pickedIndex;

                return kanjiCollection.cardsCollection.get(lastCheckIDs.get(pickedIndex));
            }
        }
        else
            return null; //Finished the deck
    }

    public String GetLastCardType() { return pickedListForKanji; }

    public void ValidateStudyCard(CardAnswer selectedAnswer, KanjiCard currentCard)
    {
        //Increase card score based on selected answer
        int cardScore = currentCard.masterScore;
        switch (selectedAnswer)
        {
            case Again:
                cardScore = 1;
                break;
            case Hard:
                cardScore++;
                break;
            case Good:
                cardScore += 5;
                break;
            case Easy:
                //If it is a last check card, it will have Again & Easy buttons, so we don't want to increase it's score any more than that
                if(!pickedListForKanji.equals("LastCheck"))
                    cardScore += 10;
                break;
            case Learn:
                cardScore = 1;
                break;
            case Skip:
                cardScore += 10;
                break;
        }
        currentCard.masterScore = cardScore;
        currentCard.lastReviewDate = currentDate;

        int removedElem;
        switch(pickedListForKanji)
        {
            case "Refresh":
                removedElem = refreshCardsIDs.get(pickedListIndex);
                refreshCardsIDs.remove(pickedListIndex);
                if(cardScore < 10)
                    inReviewIDs.add(removedElem);
                break;
            case "Review":
                if(cardScore >= 10)
                {
                    removedElem = inReviewIDs.get(pickedListIndex);
                    inReviewIDs.remove(pickedListIndex);
                    lastCheckIDs.add(removedElem);

                    if(inReviewIDs.size() > 0)
                    {
                        reviewIterator--;
                        if (reviewIterator < 0)
                            reviewIterator = inReviewIDs.size() - 1;
                    }

                    if (inReviewIDs.size() == 0 && refreshCardsIDs.size() == 0 && newCardsIDs.size() == 0)
                        clearLastCheck = true;

                    if(inReviewIDs.size() == 0)
                        refreshCardsEmptied = true;
                }
                break;
            case "New":
                removedElem = newCardsIDs.get(pickedListIndex);
                newCardsIDs.remove(pickedListIndex);
                if(cardScore >= 10)
                    lastCheckIDs.add(removedElem);
                else
                    inReviewIDs.add(removedElem);
                break;
            case "LastCheck":
                if(currentCard.masterScore >= 10)
                {
                    currentCard.nextReviewDays = GetNextReviewDays(currentCard.masterScore);
                    lastCheckIDs.remove(pickedListIndex);
                }
                else
                {
                    removedElem = lastCheckIDs.get(pickedListIndex);
                    lastCheckIDs.remove(pickedListIndex);
                    inReviewIDs.add(removedElem);

                    if(inReviewIDs.size() > inReviewLimit)
                        clearLastCheck = false;
                }

                if(lastCheckIDs.size() == 0 && refreshCardsIDs.size() == 0 && newCardsIDs.size() == 0 && inReviewIDs.size() > 0)
                    clearLastCheck = false;
                break;
        }
        cardsStudiedToday++;
    }

    public int GetNextReviewDays(int cardScore)
    {
        if(cardScore >= 10 && cardScore <= 30)
            return (int) ((cardScore - 10) * 1.45 + 1);
        else
        {
            int val = (int)(cardScore - 30) * 3 + 30;
            if(val > 90)
                return 90;
            return val;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void CalculateTimePassed()
    {
        LocalTime endTime = LocalTime.now();
        Duration currentStudySession = Duration.between(studyStartTime, endTime);

        long seconds = 0;

        if(totalSpentTimeStudying.equals("00:00:00"))
            seconds = currentStudySession.getSeconds();
        else
        {
            Duration dayTotal = Duration.between(LocalTime.MIN, LocalTime.parse(totalSpentTimeStudying));
            Duration newTotal = dayTotal.plus(currentStudySession);
            seconds = newTotal.getSeconds();
        }

        long HH = seconds / 3600;
        long MM = (seconds % 3600) / 60;
        long SS = seconds % 60;

        totalSpentTimeStudying = String.format("%02d:%02d:%02d", HH, MM, SS);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void ResetProgress()
    {
        kanjiCollection.ResetAllCards();
        newCardsIDs = new ArrayList<>();
        refreshCardsIDs = new ArrayList<>();
        inReviewIDs = new ArrayList<>();
        lastCheckIDs = new ArrayList<>();

        cardsStudiedToday = 0;
        totalSpentTimeStudying = "00:00:00";

        dataSaver.SaveData(currentDate, true);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void RecalculateDayReview()
    {
        int diff = newCardsIDs.size() - newCardsLimit;
        if(diff > 0)
        {
            newCardsIDs.subList(newCardsIDs.size() - diff, newCardsIDs.size()).clear();
        }

        diff = refreshCardsIDs.size() - refreshCardsLimit;
        if(diff > 0)
        {
            refreshCardsIDs.subList(refreshCardsIDs.size() - diff, refreshCardsIDs.size()).clear();
        }

        diff = inReviewIDs.size() - inReviewLimit;
        if(diff > 0)
        {
            inReviewIDs.subList(inReviewIDs.size() - diff, inReviewIDs.size()).clear();
        }

        //GenerateDayReview();
    }
}
