package com.example.japotimeapp.fragments;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.japotimeapp.MainActivity;
import com.example.japotimeapp.R;
import com.example.japotimeapp.enums.ActiveFragment;
import com.example.japotimeapp.utils.KanjiCard;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class CardBrowserFragment extends Fragment
{
    private final MainActivity mainActivity;

    private int cardsPerFrame = 10;
    private int reachedPopulationIndex = 0;
    private ArrayList<KanjiCard> cardsToPopulate;

    private DateTimeFormatter formatter;

    private TableRow.LayoutParams textViewParam1;
    private TableRow.LayoutParams textViewParam2;
    private TableRow.LayoutParams textViewParam3;
    private LinearLayout.LayoutParams textViewParam4;
    private TableLayout tableLayout;
    private TableLayout.LayoutParams tableRowParams;

    private TextInputLayout textInputLayout;

    private String sortType = "";
    private String matchString = "";
    private Boolean sortAsc = false;
    private Boolean runnableRunning = false;

    public CardBrowserFragment(MainActivity _mainActivity)
    {
        mainActivity = _mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.card_browser, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Button backBtn = view.findViewById(R.id.cardBrowserBackBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new SettingsPageFragment(mainActivity)).commit();
                mainActivity.currentActiveFragment = ActiveFragment.SettingsPage;
            }
        });

        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        textViewParam1 = new TableRow.LayoutParams(0, TableLayout.LayoutParams.MATCH_PARENT,0.2f);
        textViewParam1.setMargins(5, 15, 5, 15);

        textViewParam2 = new TableRow.LayoutParams(0, TableLayout.LayoutParams.MATCH_PARENT,0.6f);
        textViewParam2.setMargins(5, 15, 5, 15);

        textViewParam3 = new TableRow.LayoutParams(0, TableLayout.LayoutParams.MATCH_PARENT,0.2f);
        textViewParam3.setMargins(5, 15, 5, 15);

        textViewParam4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);

        tableLayout = view.findViewById(R.id.cardBrowserTable);
        tableRowParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT);

        Button sortBymasteryBtn = view.findViewById(R.id.cardBrowserSortBtn);
        sortBymasteryBtn.setOnClickListener(v -> {
            sortAsc = sortType.equals("SortByMastery") ? !sortAsc : false;
            sortType = "SortByMastery";
            FindCardsToPopulate(view);
        });

        Button soryByTime = view.findViewById(R.id.cardBrowserTimeSortBtn);
        soryByTime.setOnClickListener(v -> {
            sortAsc = sortType.equals("SortByTime") ? !sortAsc : false;
            sortType = "SortByTime";
            FindCardsToPopulate(view);
        });

        textInputLayout = view.findViewById(R.id.cardBrowserSearchInput);
        Button searchBtn = view.findViewById(R.id.cardBrowserSearchBtn);
        searchBtn.setOnClickListener(v -> {
            matchString = textInputLayout.getEditText().getText().toString();
            System.out.println(matchString);
            FindCardsToPopulate(view);
        });

        FindCardsToPopulate(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void FindCardsToPopulate(View view)
    {
        reachedPopulationIndex = 0;
        cardsToPopulate = new ArrayList<>();
        tableLayout.removeAllViews();

        for (int index = 0; index < mainActivity.kanjiCollection.cardsCollection.size(); index++)
        {
            Boolean addCard = false;
            if(matchString.equals(""))
                addCard = true;
            else
            {
                if(mainActivity.kanjiCollection.cardsCollection.get(index).kanji.contains(matchString))
                    addCard = true;
                else if(mainActivity.kanjiCollection.cardsCollection.get(index).reading.contains(matchString))
                    addCard = true;
                else
                {
                    for(int index2 = 0; index2 < mainActivity.kanjiCollection.cardsCollection.get(index).meanings.size(); index2++)
                    {
                        if(mainActivity.kanjiCollection.cardsCollection.get(index).meanings.get(index2).contains(matchString))
                        {
                            addCard = true;
                            break;
                        }
                    }
                }
            }

            if(addCard)
                cardsToPopulate.add(mainActivity.kanjiCollection.cardsCollection.get(index));
        }

        if(sortType.equals("SortByMastery") || sortType.equals("SortByTime"))
        {
            for(int index = 0; index < cardsToPopulate.size() - 1; index++)
            {
                for(int index2 = 0; index2 < cardsToPopulate.size(); index2++)
                {
                    KanjiCard card = cardsToPopulate.get(index);
                    KanjiCard card2 = cardsToPopulate.get(index2);

                    Boolean swap = false;
                    if(sortType.equals("SortByMastery"))
                    {
                        if(sortAsc && card.masterScore < card2.masterScore)
                            swap = true;
                        else if(!sortAsc && card.masterScore > card2.masterScore)
                            swap = true;
                    }
                    else
                    {
                        LocalDate date1, date2;
                        date1 = card.lastReviewDate == null ? LocalDate.MIN : LocalDate.parse(card.lastReviewDate, formatter).plusDays(card.nextReviewDays);
                        date2 = card2.lastReviewDate == null ? LocalDate.MIN : LocalDate.parse(card2.lastReviewDate, formatter).plusDays(card2.nextReviewDays);
                        int diff = (int) ChronoUnit.DAYS.between(date1, date2);
                        if(sortAsc && diff > 0)
                            swap = true;
                        else if(!sortAsc && diff < 0)
                            swap = true;
                    }

                    if(swap)
                    {
                        cardsToPopulate.set(index, card2);
                        cardsToPopulate.set(index2, card);
                    }
                }
            }
        }

        if(!runnableRunning)
        {
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    PopulateTable(view);
                }
            }, 100);
        }
        runnableRunning = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void PopulateTable(View view)
    {
        for (int index = 0; index < cardsPerFrame && reachedPopulationIndex < cardsToPopulate.size(); index++, reachedPopulationIndex++)
        {
            TableRow tableRow = new TableRow(mainActivity);
            tableRow.setLayoutParams(tableRowParams);

            TextView textView1 = new TextView(mainActivity);
            textView1.setTextSize(20);
            textView1.setGravity(Gravity.CENTER);
            textView1.setText(cardsToPopulate.get(reachedPopulationIndex).kanji);
            textView1.setLayoutParams(textViewParam1);

            TextView textView2 = new TextView(mainActivity);
            textView2.setGravity(Gravity.LEFT);
            String meanings = "";
            for(int index2 = 0; index2 < cardsToPopulate.get(reachedPopulationIndex).meanings.size(); index2++)
            {
                meanings += "- " + cardsToPopulate.get(reachedPopulationIndex).meanings.get(index2);
                if(index2 != cardsToPopulate.get(reachedPopulationIndex).meanings.size() - 1)
                    meanings += "\n";
            }
            textView2.setText(meanings);
            textView2.setLayoutParams(textViewParam2);



            LinearLayout verticalLayout = new LinearLayout(mainActivity);
            verticalLayout.setOrientation(LinearLayout.VERTICAL);
            verticalLayout.setLayoutParams(textViewParam3);

            TextView nextReviewDate = new TextView(mainActivity);
            nextReviewDate.setTextSize(14);
            nextReviewDate.setGravity(Gravity.CENTER);
            nextReviewDate.setLayoutParams(textViewParam4);

            if(cardsToPopulate.get(reachedPopulationIndex).lastReviewDate == null)
                nextReviewDate.setText("");
            else
            {
                LocalDate date = LocalDate.parse(cardsToPopulate.get(reachedPopulationIndex).lastReviewDate, formatter);
                String nextReview = date.plusDays(cardsToPopulate.get(reachedPopulationIndex).nextReviewDays).format(formatter);
                nextReviewDate.setText(nextReview);
            }


            TextView masterScore = new TextView(mainActivity);
            masterScore.setTextSize(14);
            masterScore.setGravity(Gravity.CENTER);
            masterScore.setText("" + cardsToPopulate.get(reachedPopulationIndex).masterScore);
            masterScore.setLayoutParams(textViewParam4);

            float lerpFactor = cardsToPopulate.get(reachedPopulationIndex).masterScore / 50.0f;
            if(lerpFactor > 1.0f)
                lerpFactor = 1.0f;
            masterScore.setBackgroundColor(LerpRGB(Color.valueOf(0xff0000), Color.valueOf(0x00ff00), lerpFactor));

            verticalLayout.addView(nextReviewDate);
            verticalLayout.addView(masterScore);



            tableRow.addView(textView1);
            tableRow.addView(textView2);
            tableRow.addView(verticalLayout);

            tableLayout.addView(tableRow);
        }

        if(reachedPopulationIndex < cardsToPopulate.size())
        {
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PopulateTable(view);
                    }
                }, 100);
        }
        else
            runnableRunning = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int LerpRGB (Color a, Color b, float t)
    {
        return Color.rgb(a.red() + (b.red() - a.red()) * t, a.green() + (b.green() - a.green()) * t, a.blue() + (b.blue() - a.blue()) * t);
    }
}
