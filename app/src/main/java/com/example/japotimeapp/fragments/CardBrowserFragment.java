package com.example.japotimeapp.fragments;

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
            }
        });

        ScrollView scrollView = view.findViewById(R.id.cardBrowserScrollView);
        TableLayout tableLayout = createTableLayout();
        scrollView.addView(tableLayout);
    }

    private TableLayout createTableLayout()
    {
        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams();
        TableLayout tableLayout = new TableLayout(mainActivity);

        TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams();
        tableRowParams.setMargins(1, 1, 1, 1);
        tableRowParams.weight = 1;

        for (int i = 0; i < mainActivity.kanjiCollection.cardsCollection.size(); i++)
        {
            TableRow tableRow = new TableRow(mainActivity);
            //tableRow.setBackgroundColor(Color.BLACK);

            TextView textView1 = new TextView(mainActivity);
            textView1.setGravity(Gravity.LEFT);
            textView1.setText(mainActivity.kanjiCollection.cardsCollection.get(i).kanji);

            TextView textView2 = new TextView(mainActivity);
            textView2.setGravity(Gravity.LEFT);
            String meanings = "";
            for(int index = 0; index < mainActivity.kanjiCollection.cardsCollection.get(i).meanings.size(); index++)
            {
                meanings += "-" + mainActivity.kanjiCollection.cardsCollection.get(i).meanings.get(index);
                if(index != mainActivity.kanjiCollection.cardsCollection.get(i).meanings.size() - 1)
                    meanings += "\n";
            }
            textView2.setText(meanings);

            tableRow.addView(textView1, tableRowParams);
            tableRow.addView(textView2, tableRowParams);

            tableLayout.addView(tableRow, tableLayoutParams);
        }

        return tableLayout;
    }
}
