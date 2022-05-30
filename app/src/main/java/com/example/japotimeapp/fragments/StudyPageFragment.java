package com.example.japotimeapp.fragments;


import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.japotimeapp.MainActivity;
import com.example.japotimeapp.R;
import com.example.japotimeapp.enums.ActiveFragment;
import com.example.japotimeapp.enums.CardAnswer;
import com.example.japotimeapp.utils.KanjiCard;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private TextView studyPageCardsLastCheck;
    private TextView studyPageCardsNew;
    private TextView studyPageCardsReview;
    private TextView studyPageCardsRefresh;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mainActivity.dailyReview.studyStartTime = LocalTime.now();

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

        studyPageCardsLastCheck =  view.findViewById(R.id.studyPageCardsLastCheck);
        studyPageCardsNew =  view.findViewById(R.id.studyPageCardsNew);
        studyPageCardsReview =  view.findViewById(R.id.studyPageCardsReview);
        studyPageCardsRefresh =  view.findViewById(R.id.studyPageCardsRefresh);

        studyPageCardsNew.setText("" + mainActivity.dailyReview.GetNewCardsCount());
        studyPageCardsRefresh.setText("" + mainActivity.dailyReview.GetRefreshCardsCount());
        studyPageCardsReview.setText("" + mainActivity.dailyReview.GetInReviewCardsCount());
        studyPageCardsLastCheck.setText("" + mainActivity.dailyReview.GetLastCheckCardsCount());

        backMeaningAdapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_list_item_1, backMeaningList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                tv.setTextColor(getResources().getColor(R.color.normalText));
                return view;
            }
        };;
        backMeaning.setAdapter(backMeaningAdapter);

        Button backBtn = view.findViewById(R.id.studyPageBackBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                mainActivity.isStudyPageActive = false;
                mainActivity.dailyReview.CalculateTimePassed();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new MainPageFragment(mainActivity)).commit();
                mainActivity.currentActiveFragment = ActiveFragment.MainPage;
            }
        });

        againBtn.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { ParseCardAnswer(CardAnswer.Again); }});
        hardBtn.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { ParseCardAnswer(CardAnswer.Hard); }});
        goodBtn.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { ParseCardAnswer(CardAnswer.Good); }});
        easyBtn.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { ParseCardAnswer(CardAnswer.Easy); }});
        learnBtn.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { ParseCardAnswer(CardAnswer.Learn); }});
        skipBtn.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { ParseCardAnswer(CardAnswer.Skip); }});

        showAnswerBtn.setVisibility(View.VISIBLE);
        showAnswerBtn.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { ShowCardBack(); }});

        PickNextCard();
    }

    @SuppressLint("SetTextI18n")
    public void ShowCardBack()
    {
        showAnswerBtn.setVisibility(View.GONE);
        easyBtn.setText("EASY");
        hardBtn.setText("HARD");
        goodBtn.setText("GOOD");

        String cardType = mainActivity.dailyReview.GetLastCardType();
        switch(cardType)
        {
            case "Refresh":
                againBtn.setVisibility(View.VISIBLE);
                hardBtn.setVisibility(View.VISIBLE);
                goodBtn.setVisibility(View.VISIBLE);
                easyBtn.setVisibility(View.VISIBLE);

                if(currentCard.masterScore > 10)
                {
                    hardBtn.setText("HARD - " + GetNextDateDisplay(currentCard.masterScore + 1));
                    goodBtn.setText("GOOD - " + GetNextDateDisplay(currentCard.masterScore + 5));
                    easyBtn.setText("EASY - " + GetNextDateDisplay(currentCard.masterScore + 10));
                }
                break;
            case "Review":
                againBtn.setVisibility(View.VISIBLE);
                hardBtn.setVisibility(View.VISIBLE);
                goodBtn.setVisibility(View.VISIBLE);
                easyBtn.setVisibility(View.VISIBLE);
                break;
            case "New":
                learnBtn.setVisibility(View.VISIBLE);
                skipBtn.setVisibility(View.VISIBLE);
                break;
            case "LastCheck":
                againBtn.setVisibility(View.VISIBLE);
                easyBtn.setVisibility(View.VISIBLE);

                easyBtn.setText("EASY - " + GetNextDateDisplay(currentCard.masterScore));
                break;
        }

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

    public String GetNextDateDisplay(int cardScore)
    {
        int nextReviewDate = mainActivity.dailyReview.GetNextReviewDays(cardScore);
        String nextReviewText = nextReviewDate + "d";
        return nextReviewText;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void ParseCardAnswer(CardAnswer selectedAnswer)
    {
        mainActivity.dailyReview.ValidateStudyCard(selectedAnswer, currentCard);

        studyPageCardsNew.setText("" + mainActivity.dailyReview.GetNewCardsCount());
        studyPageCardsRefresh.setText("" + mainActivity.dailyReview.GetRefreshCardsCount());
        studyPageCardsReview.setText("" + mainActivity.dailyReview.GetInReviewCardsCount());
        studyPageCardsLastCheck.setText("" + mainActivity.dailyReview.GetLastCheckCardsCount());

        PickNextCard();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void PickNextCard()
    {
        frontCard.setVisibility(View.VISIBLE);
        backCard.setVisibility(View.GONE);

        currentCard = mainActivity.dailyReview.GetNextStudyCard();

        //End of the session
        if(currentCard == null)
        {
            mainActivity.isStudyPageActive = false;
            mainActivity.dailyReview.CalculateTimePassed();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new MainPageFragment(mainActivity)).commit();
            mainActivity.currentActiveFragment = ActiveFragment.MainPage;
            return;
        }

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
