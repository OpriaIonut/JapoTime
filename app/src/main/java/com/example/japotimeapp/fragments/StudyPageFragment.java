package com.example.japotimeapp.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.japotimeapp.MainActivity;
import com.example.japotimeapp.R;
import com.example.japotimeapp.enums.CardAnswer;
import com.example.japotimeapp.utils.KanjiCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class StudyPageFragment extends Fragment
{
    private final MainActivity mainActivity;

    private ConstraintLayout frontCard;
    private ConstraintLayout backCard;

    private Button againBtn;
    private Button hardBtn;
    private Button goodBtn;
    private Button easyBtn;
    private Button learnBtn;
    private Button skipBtn;
    private Button showAnswerBtn;

    private TextView frontKanji;
    private TextView frontSentence;

    private TextView backKanji;
    private TextView backSentence;
    private TextView backReading;
    private ListView backMeaning;
    private List<String> backMeaningList = new ArrayList<>();
    private ArrayAdapter<String> backMeaningAdapter;

    private KanjiCard currentCard;

    public StudyPageFragment(MainActivity _mainActivity)
    {
        mainActivity = _mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.study_page, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        frontCard = view.findViewById(R.id.studyPageFront);
        backCard =  view.findViewById(R.id.studyPageBack);

        againBtn =  view.findViewById(R.id.studyPageAgainBtn);
        hardBtn =  view.findViewById(R.id.studyPageHardBtn);
        goodBtn =  view.findViewById(R.id.studyPageGoodBtn);
        easyBtn =  view.findViewById(R.id.studyPageEasyBtn);
        learnBtn =  view.findViewById(R.id.studyPageLearnBtn);
        skipBtn =  view.findViewById(R.id.studyPageSkipBtn);
        showAnswerBtn =  view.findViewById(R.id.studyPageShowAnswerBtn);

        frontKanji =  view.findViewById(R.id.studyPageFrontKanji);
        frontSentence =  view.findViewById(R.id.studyPageFrontSentence);

        backKanji =  view.findViewById(R.id.studyPageBackKanji);
        backSentence =  view.findViewById(R.id.studyPageBackSentence);
        backReading =  view.findViewById(R.id.studyPageBackReading);
        backMeaning =  view.findViewById(R.id.studyPageBackMeaning);

        backMeaningAdapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_list_item_1, backMeaningList);
        backMeaning.setAdapter(backMeaningAdapter);

        Button backBtn = view.findViewById(R.id.studyPageBackBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new MainPageFragment(mainActivity)).commit();
            }
        });

        againBtn.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { parseCardAnswer(CardAnswer.Again); }});
        hardBtn.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { parseCardAnswer(CardAnswer.Hard); }});
        goodBtn.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { parseCardAnswer(CardAnswer.Good); }});
        easyBtn.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { parseCardAnswer(CardAnswer.Easy); }});
        learnBtn.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { parseCardAnswer(CardAnswer.Learn); }});
        skipBtn.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { parseCardAnswer(CardAnswer.Skip); }});

        showAnswerBtn.setVisibility(View.VISIBLE);
        showAnswerBtn.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { showCardBack(); }});

        pickNextCard();
    }

    public void showCardBack()
    {
        showAnswerBtn.setVisibility(View.GONE);
        learnBtn.setVisibility(View.VISIBLE);
        skipBtn.setVisibility(View.VISIBLE);

        frontCard.setVisibility(View.GONE);
        backCard.setVisibility(View.VISIBLE);

        backKanji.setText(currentCard.kanji);
        backSentence.setText(currentCard.sentence);
        backReading.setText(currentCard.reading);

        backMeaningList.clear();
        for(int index = 0; index < currentCard.meanings.size(); index++)
            backMeaningList.add(currentCard.meanings.get(index));
        backMeaningAdapter.notifyDataSetChanged();
    }

    public void parseCardAnswer(CardAnswer selectedAnswer)
    {
        switch (selectedAnswer) {
            case Again:
                break;
            case Hard:
                break;
            case Good:
                break;
            case Easy:
                break;
            case Learn:
                break;
            case Skip:
                break;
        }
        pickNextCard();
    }

    public void pickNextCard()
    {
        frontCard.setVisibility(View.VISIBLE);
        backCard.setVisibility(View.GONE);

        int randomCard = ThreadLocalRandom.current().nextInt(0, mainActivity.cardsCollection.size());
        currentCard = mainActivity.cardsCollection.get(randomCard);

        frontKanji.setText(currentCard.kanji);
        frontSentence.setText(currentCard.sentence);

        showAnswerBtn.setVisibility(View.VISIBLE);
        learnBtn.setVisibility(View.GONE);
        skipBtn.setVisibility(View.GONE);
        againBtn.setVisibility(View.GONE);
        hardBtn.setVisibility(View.GONE);
        goodBtn.setVisibility(View.GONE);
        easyBtn.setVisibility(View.GONE);
    }
}
