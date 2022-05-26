package com.example.japotimeapp.fragments;

import android.animation.ArgbEvaluator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
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

import java.util.ArrayList;
import java.util.List;

public class CardBrowserFragment extends Fragment
{
    private final MainActivity mainActivity;

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

        createTableLayout(view);
    }

    @SuppressLint("ResourceAsColor")
    private void createTableLayout(View view)
    {
        TableRow.LayoutParams textViewParam1 = new TableRow.LayoutParams(0, TableLayout.LayoutParams.MATCH_PARENT,0.3f);
        textViewParam1.setMargins(5, 15, 5, 15);

        TableRow.LayoutParams textViewParam2 = new TableRow.LayoutParams(0, TableLayout.LayoutParams.MATCH_PARENT,0.7f);
        textViewParam2.setMargins(5, 15, 5, 15);

        TableLayout tableLayout = view.findViewById(R.id.cardBrowserTable);
//        tableLayout.setBackgroundColor(Color.parseColor("#ccccdd"));
        TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT);

        for (int index = 0; index < mainActivity.kanjiCollection.cardsCollection.size(); index++)
        {
            TableRow tableRow = new TableRow(mainActivity);
            tableRow.setLayoutParams(tableRowParams);

            TextView textView1 = new TextView(mainActivity);
            textView1.setTextSize(20);
            textView1.setGravity(Gravity.CENTER);
            textView1.setText(mainActivity.kanjiCollection.cardsCollection.get(index).kanji);
            textView1.setLayoutParams(textViewParam1);
//            textView1.setBackgroundColor(R.color.grayBlueish);

            TextView textView2 = new TextView(mainActivity);
            textView2.setGravity(Gravity.LEFT);
            String meanings = "";
            for(int index2 = 0; index2 < mainActivity.kanjiCollection.cardsCollection.get(index).meanings.size(); index2++)
            {
                meanings += "- " + mainActivity.kanjiCollection.cardsCollection.get(index).meanings.get(index2);
                if(index2 != mainActivity.kanjiCollection.cardsCollection.get(index).meanings.size() - 1)
                    meanings += "\n";
            }
            textView2.setText(meanings);
            textView2.setLayoutParams(textViewParam2);
//            textView2.setBackgroundColor(R.color.grayBlueish);

            tableRow.addView(textView1);
            tableRow.addView(textView2);

            tableLayout.addView(tableRow);
        }
    }
}
