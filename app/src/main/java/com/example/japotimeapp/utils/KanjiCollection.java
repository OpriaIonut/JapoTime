package com.example.japotimeapp.utils;

import java.util.ArrayList;
import java.util.List;

public class KanjiCollection
{
    public List<KanjiCard> cardsCollection;
    private List<String> digitalOceanCards;

    public KanjiCollection(List<String> result, DataSaver dataSaver)
    {
        LoadKanjiFromSavedData(dataSaver);
        LoadKanjiFromDigitalOcean(result);
    }

    private void LoadKanjiFromSavedData(DataSaver dataSaver)
    {
        UserData loadedData = dataSaver.loadedData;

        if(loadedData == null)
        {
            cardsCollection = new ArrayList<KanjiCard>();
        }
        else
        {
            cardsCollection = loadedData.kanjiCards;
        }
    }

    private void LoadKanjiFromDigitalOcean(List<String> result)
    {
        digitalOceanCards = result;

        for(int index = 0; index < result.size(); index++)
        {
            KanjiCard newCard = new KanjiCard();
            newCard.ExtractDefinitions(result.get(index));

            Boolean cardExists = false;
            for(int index2 = 0; index2 < cardsCollection.size(); index2++)
            {
                if(cardsCollection.get(index2).IsEqual(newCard))
                {
                    cardExists = true;
                    break;
                }
            }
            if(!cardExists)
            {
                cardsCollection.add(newCard);
            }
        }
    }

    public void ResetAllCards()
    {
        cardsCollection = new ArrayList<>();
        LoadKanjiFromDigitalOcean(digitalOceanCards);
    }
}
