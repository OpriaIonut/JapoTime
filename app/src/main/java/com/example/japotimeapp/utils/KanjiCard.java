package com.example.japotimeapp.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KanjiCard
{
    public String kanji;
    public String sentence;
    public String reading;
    public List<String> meanings;

    public Date lastReviewDate;
    public int nextReviewDays;
    public int masterScore;

    public KanjiCard()
    {
        kanji = "";
        sentence = "";
        meanings = new ArrayList<>();
        lastReviewDate = null;
        nextReviewDays = 0;
        masterScore = 0;
    }

    public Boolean IsEqual(KanjiCard card)
    {
        return this.kanji.equals(card.kanji) && this.sentence.equals(card.sentence) && this.reading.equals(card.reading);
    }


    //Remove html unicode characters using this website: https://www.textfixer.com/html/html-character-encoding.php
    public void ExtractDefinitions(String cardInput)
    {
        String parsedInput = cardInput;

        //Find kanji
        int kanjiEnd = parsedInput.indexOf("\"");
        kanji = cardInput.substring(0, kanjiEnd);
        parsedInput = parsedInput.replace(kanji, "");
        kanji = kanji.replaceAll("\t", "");

        //Find meanings if they exist
        if(parsedInput.contains("<ol>"))
        {
            String meaningText = parsedInput.substring(parsedInput.indexOf("<ol>") + 4, parsedInput.indexOf("</ol>"));
            while (!meaningText.equals("")) {
                int elementEnd = meaningText.indexOf("</li>");
                String element = meaningText.substring(4, elementEnd);

                meanings.add(element);
                meaningText = meaningText.replace("<li>" + element + "</li>", "");
            }
        }
        int divEnd = parsedInput.indexOf("</div>") + 7;
        parsedInput = parsedInput.substring(divEnd);


        //Split the text based on tab (standard separator for this format) & find the first entry that isn't empty. That will be the sentence
        String[] str = parsedInput.split("\t");
        for(int index =0; index < str.length; index++)
        {
            if(str[index].length() > 0)
            {
                sentence = str[index];
                break;
            }
        }

        //If the text contains the ruby tag, then it means it has a reading
        reading = "";
        if(parsedInput.contains("<ruby>"))
        {
            //Find the ruby text (second from the last, excluding empty entries)
            String readingText = "";
            int counter = 0;
            for(int index = str.length - 1; index >= 0; index--)
            {
                if(str[index].length() > 0)
                {
                    counter++;
                    if(counter == 2)
                    {
                        readingText = str[index];
                        break;
                    }
                }
            }

            //Go through the ruby text
            int nextRuby = readingText.indexOf("<ruby>");
            while(nextRuby != -1)
            {
                //Between ruby tags, there can be katakana/hiragana, so add empty spaces for those
                for(int index = 0; index < nextRuby; index++)
                    reading += readingText.charAt(index);

                //Find the reading between rt tags
                int nextRT = readingText.indexOf("<rt>") + 4;
                int nextRTEnd = readingText.indexOf("</rt>");
                reading += readingText.substring(nextRT, nextRTEnd);

                //Replace the text & search for next ruby tag
                int nextRubyEnd = readingText.indexOf("</ruby>") + 7;
                readingText = readingText.substring(nextRubyEnd);
                nextRuby = readingText.indexOf("<ruby>");
            }
            //Some hiragana/katakana may be after the last ruby tag, so put empty spaces for those
            for(int index = 0; index < readingText.length(); index++)
                reading += readingText.charAt(index);
        }
    }

    @Override
    public String toString() {
        return "KanjiCard{ " +
                "kanji = '" + kanji + '\'' +
                ", sentence = '" + sentence + '\'' +
                ", reading = '" + reading + '\'' +
                ", meaningCount = " + meanings.size() +
                ", meanings = " + meanings +
                '}';
    }
}
