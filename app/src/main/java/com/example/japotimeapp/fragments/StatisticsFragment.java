package com.example.japotimeapp.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.japotimeapp.MainActivity;
import com.example.japotimeapp.R;
import com.example.japotimeapp.enums.ActiveFragment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class StatisticsFragment extends Fragment
{
    private MainActivity mainActivity;

    public StatisticsFragment(MainActivity _mainActivity)
    {
        mainActivity = _mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.statistics_page, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        Button backBtn = view.findViewById(R.id.statisticsBackBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new SettingsPageFragment(mainActivity)).commit();
                mainActivity.currentActiveFragment = ActiveFragment.SettingsPage;
            }
        });

        BarChart barChart = view.findViewById(R.id.totalCardsBarChart);
        CreateBarChart(barChart);

        PieChart pieChart = view.findViewById(R.id.cardMasteryPieChart);
        CreatePieChart(pieChart);
    }

    private void CreateBarChart(BarChart barChart)
    {
        ArrayList totalCardsData = new ArrayList();
        int totalCards = mainActivity.kanjiCollection.cardsCollection.size();
        int knownCards = 0;
        int masteredCards = 0;
        for(int index = 0; index < totalCards; index++)
        {
            if(mainActivity.kanjiCollection.cardsCollection.get(index).masterScore >= 50)
                masteredCards++;
            else if(mainActivity.kanjiCollection.cardsCollection.get(index).masterScore > 0)
                knownCards++;
        }
        totalCardsData.add(new BarEntry(3f, totalCards));
        totalCardsData.add(new BarEntry(5f, knownCards));
        totalCardsData.add(new BarEntry(7f, masteredCards));

        BarDataSet barDataSet = new BarDataSet(totalCardsData, "Total Cards");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        ArrayList colors = new ArrayList();
        colors.add(Color.WHITE);
        colors.add(Color.argb(255, 255, 125, 0));
        colors.add(Color.CYAN);

        barDataSet.setColors(colors);
        barDataSet.setValueTextColor(Color.WHITE);
        barDataSet.setValueTextSize((16f));

        barChart.getAxisLeft().setTextColor(Color.WHITE);
        barChart.getAxisRight().setTextColor(Color.WHITE);
        barChart.getXAxis().setDrawLabels(false);
        barChart.getLegend().setTextColor(Color.WHITE);

        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.animateY(1000);
    }

    private void CreatePieChart(PieChart pieChart)
    {
        int unknownCards = 0;       //Mastery 0
        int justLearnedCards = 0;   //Mastery 10-20
        int averageCards = 0;       //Mastery 20-30
        int goodCards = 0;          //Mastery 30-40
        int veryGoodCards = 0;      //Mastery 40-49
        int masteredCards = 0;      //Mastery >50

        ArrayList cardsData = new ArrayList();
        int totalCards = mainActivity.kanjiCollection.cardsCollection.size();
        for(int index = 0; index < totalCards; index++)
        {
            int masterScore = mainActivity.kanjiCollection.cardsCollection.get(index).masterScore;

            if(masterScore == 0)
                unknownCards++;
            else if(masterScore >= 10 && masterScore < 20)
                justLearnedCards++;
            else if(masterScore >= 20 && masterScore < 30)
                averageCards++;
            else if(masterScore >= 30 && masterScore < 40)
                goodCards++;
            else if(masterScore >= 40 && masterScore < 50)
                veryGoodCards++;
            else if(masterScore >= 50)
                masteredCards++;
        }

        ArrayList colorsArray = new ArrayList();

        if(unknownCards > 0)
        {
            cardsData.add(new PieEntry(unknownCards, "Unknown"));
            colorsArray.add(Color.WHITE);
        }
        if(justLearnedCards > 0)
        {
            cardsData.add(new PieEntry(justLearnedCards, "Just Learned"));
            colorsArray.add(Color.parseColor("#fc6603"));
        }
        if(averageCards > 0)
        {
            cardsData.add(new PieEntry(averageCards, "Average"));
            colorsArray.add(Color.parseColor("#fca103"));
        }
        if(goodCards > 0)
        {
            cardsData.add(new PieEntry(goodCards, "Good"));
            colorsArray.add(Color.parseColor("#fce303"));
        }
        if(veryGoodCards > 0)
        {
            cardsData.add(new PieEntry(veryGoodCards, "Very Good"));
            colorsArray.add(Color.parseColor("#b5fc03"));
        }
        if(masteredCards > 0)
        {
            cardsData.add(new PieEntry(masteredCards, "Mastered"));
            colorsArray.add(Color.parseColor("#2dfc03"));
        }

        PieDataSet pieDataSet = new PieDataSet(cardsData, "");
        pieDataSet.setColors(colorsArray);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(14f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.getLegend().setTextColor(Color.WHITE);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setDrawHoleEnabled(false);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("");
        pieChart.animate();
    }
}
