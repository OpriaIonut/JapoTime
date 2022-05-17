package com.example.japotimeapp.utils;

import android.os.Build;
import android.util.Pair;

import androidx.annotation.RequiresApi;

import com.example.japotimeapp.enums.CardAnswer;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DailyReview
{
    private DataSaver dataSaver;
    private KanjiCollection kanjiCollection;

    private List<Integer> newCardsIDs;
    private List<Integer> refreshCardsIDs;
    private List<Integer> inReviewIDs;
    private List<Integer> lastCheckIDs;

    private int newCardsLimit = 20;
    private int refreshCardsLimit = 40;
    private int inReviewLimit = 10;
    private int newCardsLearningBatch = 5;

    private LocalDate currentDate;
    private Random randomGenerator;

    private Boolean refreshCardsEmptied = false;
    private String pickedListForKanji = "";
    private int pickedListIndex = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public DailyReview(DataSaver _dataSaver, KanjiCollection _kanjiCollection)
    {
        dataSaver = _dataSaver;
        kanjiCollection = _kanjiCollection;
        currentDate = java.time.LocalDate.now();
        randomGenerator = new Random();

        if(dataSaver.loadedData == null || dataSaver.loadedData.lastOpenDate == null || dataSaver.loadedData.lastOpenDate != currentDate)
        {
            GenerateDayReview();
        }
    }

    public int GetNewCardsCount() { return newCardsIDs.size(); }
    public int GetRefreshCardsCount() { return refreshCardsIDs.size(); }
    public int GetInReviewCardsCount() { return inReviewIDs.size(); }
    public int GetLastCheckCardsCount() { return lastCheckIDs.size(); }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void GenerateDayReview()
    {
        newCardsIDs = new ArrayList<>();
        refreshCardsIDs = new ArrayList<>();
        inReviewIDs = new ArrayList<>();
        lastCheckIDs = new ArrayList<>();

        //Find the kanji that should be reviewed
        List<Pair<Integer, Integer>> kanjiToReview = new ArrayList<>();
        for(int index = 0; index < kanjiCollection.cardsCollection.size(); index++)
        {
            KanjiCard currentCard = kanjiCollection.cardsCollection.get(index);

            //If it is a card that hasn't been learned, then add it to the list
            if(currentCard.lastReviewDate == null)
            {
                if(newCardsIDs.size() < newCardsLimit)
                {
                    newCardsIDs.add(index);
                }
            }
            else
            {
                //Otherwise, if it has been learned, check if it should be learned now or not
                LocalDate reviewDate = currentDate.plusDays(currentCard.nextReviewDays);
                int reviewDayDifference = (int) ChronoUnit.DAYS.between(currentDate, reviewDate);
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

        int cardsFound = Math.min(refreshCardsLimit, kanjiToReview.size());
        for(int index = 0; index < cardsFound; index++)
        {
            refreshCardsIDs.add(kanjiToReview.get(index).first);
        }

        if(refreshCardsIDs.size() == 0)
            refreshCardsEmptied = true;
    }

    public KanjiCard GetNextStudyCard()
    {
        //If we still have space for items in review
        if(inReviewIDs.size() < inReviewLimit && newCardsIDs.size() > 0)
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
                //If we finished refresh cards, we need to empty review cards before moving on to new cards
                int pickedIndex = randomGenerator.nextInt(inReviewIDs.size());
                pickedListForKanji = "Review";
                pickedListIndex = pickedIndex;
                return kanjiCollection.cardsCollection.get(inReviewIDs.get(pickedIndex));
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
        else if (inReviewIDs.size() > 0)
        {
            //If we reached review limit, add new cards
            int pickedIndex = randomGenerator.nextInt(inReviewIDs.size());
            pickedListForKanji = "Review";
            pickedListIndex = pickedIndex;
            return kanjiCollection.cardsCollection.get(inReviewIDs.get(pickedIndex));
        }
        else if(lastCheckIDs.size() > 0)
        {
            int pickedIndex = randomGenerator.nextInt(lastCheckIDs.size());
            pickedListForKanji = "LastCheck";
            pickedListIndex = pickedIndex;
            return kanjiCollection.cardsCollection.get(lastCheckIDs.get(pickedIndex));
        }
        else
            return null; //Finished the deck
    }

    public String GetLastCardType() { return pickedListForKanji; }

    public void ValidateStudyCard(CardAnswer selectedAnswer, KanjiCard currentCard)
    {
        //Don't forget to set refreshCardsEmptied to true when finishing with refresh set

        //Increase card score based on selected answer
        int cardScore = currentCard.masterScore;
        switch (selectedAnswer)
        {
            case Again:
                cardScore = currentCard.masterScore >= 5 ? 5 : 1;
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

        int removedElem;
        switch(pickedListForKanji)
        {
            case "Refresh":
                removedElem = refreshCardsIDs.get(pickedListIndex);
                refreshCardsIDs.remove(pickedListIndex);
                inReviewIDs.add(removedElem);
                break;
            case "Review":
                if(cardScore >= 10)
                {
                    removedElem = inReviewIDs.get(pickedListIndex);
                    inReviewIDs.remove(pickedListIndex);
                    lastCheckIDs.add(removedElem);

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
                    lastCheckIDs.remove(pickedListIndex);
                }
                else
                {
                    removedElem = lastCheckIDs.get(pickedListIndex);
                    lastCheckIDs.remove(pickedListIndex);
                    inReviewIDs.add(removedElem);
                }
                break;
        }
    }
}
