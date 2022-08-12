package com.example.japotimeapp.fragments;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.japotimeapp.MainActivity;
import com.example.japotimeapp.R;
import com.example.japotimeapp.enums.ActiveFragment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class StatisticsFragment extends Fragment
{
    private MainActivity mainActivity;

    private int historyOffset = 30;
    private LineChart cardReviewLineChart;
    private LineChart timeReviewLineChart;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
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

        cardReviewLineChart = view.findViewById(R.id.dailyCardProgressLineChart);
        timeReviewLineChart = view.findViewById(R.id.dailyTimeProgressLineChart);
        CreateCardReviewLineChart();
        CreateTimeReviewLineChart();


        Button oneWeekBtn = view.findViewById(R.id.statisticsDailyReviewWeekBtn);
        oneWeekBtn.setOnClickListener(v -> { historyOffset = 7; CreateCardReviewLineChart(); CreateTimeReviewLineChart(); });

        Button oneMonthBtn = view.findViewById(R.id.statisticsDailyReviewMonthBtn);
        oneMonthBtn.setOnClickListener(v -> { historyOffset = 30; CreateCardReviewLineChart(); CreateTimeReviewLineChart(); });

        Button threeMonthsBtn = view.findViewById(R.id.statisticsDailyReview3MonthsBtn);
        threeMonthsBtn.setOnClickListener(v -> { historyOffset = 90; CreateCardReviewLineChart(); CreateTimeReviewLineChart(); });

        Button oneYearBtn = view.findViewById(R.id.statisticsDailyReviewYearBtn);
        oneYearBtn.setOnClickListener(v -> { historyOffset = 365; CreateCardReviewLineChart(); CreateTimeReviewLineChart(); });
    }

    private void CreateBarChart(BarChart barChart)
    {
        ArrayList totalCardsData = new ArrayList();
        int totalCards = mainActivity.kanjiCollection.cardsCollection.size();
        int knownCards = 0;
        int masteredCards = 0;
        int deletedCards = 0;
        for(int index = 0; index < totalCards; index++)
        {
            if(mainActivity.kanjiCollection.cardsCollection.get(index).isCardDeleted)
            {
                deletedCards++;
                continue;
            }

            if(mainActivity.kanjiCollection.cardsCollection.get(index).masterScore >= 50)
                masteredCards++;
            else if(mainActivity.kanjiCollection.cardsCollection.get(index).masterScore > 0)
                knownCards++;
        }
        totalCards -= deletedCards;

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
            if(mainActivity.kanjiCollection.cardsCollection.get(index).isCardDeleted)
                continue;

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
        pieChart.setDrawEntryLabels(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("");
        pieChart.animate();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void CreateCardReviewLineChart()
    {
        ArrayList reviewedCardsData = new ArrayList();

        cardReviewLineChart.invalidate();
        cardReviewLineChart.clear();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate currentDate = LocalDate.parse(mainActivity.currentDate, formatter);;
        LocalDate startDate = currentDate.minusDays(historyOffset);

        for(int index = 0; index < historyOffset; index++)
        {
            String dataDate = startDate.plusDays(index).format(formatter);
            if(mainActivity.dataSaver.loadedData.studiedCardsHistory.containsKey(dataDate))
                reviewedCardsData.add(new Entry(index, mainActivity.dataSaver.loadedData.studiedCardsHistory.get(dataDate)));
            else
                reviewedCardsData.add(new Entry(index, 0));
        }

        reviewedCardsData.add(new Entry(historyOffset, mainActivity.dailyReview.cardsStudiedToday));

        LineDataSet set1 = new LineDataSet(reviewedCardsData, "Cards Reviewed");
        set1.setDrawCircles(false);
        set1.setValueTextColor(Color.WHITE);

        cardReviewLineChart.getLegend().setTextColor(Color.WHITE);
        cardReviewLineChart.getAxisLeft().setTextColor(Color.WHITE);
        cardReviewLineChart.getAxisRight().setTextColor(Color.WHITE);
        cardReviewLineChart.getXAxis().setDrawLabels(false);
        cardReviewLineChart.getLegend().setTextColor(Color.WHITE);
        cardReviewLineChart.getDescription().setEnabled(false);

        LineData data = new LineData(set1);
        cardReviewLineChart.setData(data);

        cardReviewLineChart.animateX(1000);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void CreateTimeReviewLineChart()
    {
        ArrayList reviewTimeData = new ArrayList();

        timeReviewLineChart.invalidate();
        timeReviewLineChart.clear();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate currentDate = LocalDate.parse(mainActivity.currentDate, formatter);;
        LocalDate startDate = currentDate.minusDays(historyOffset);

        for(int index = 0; index < historyOffset; index++)
        {
            String dataDate = startDate.plusDays(index).format(formatter);
            if(mainActivity.dataSaver.loadedData.studiedCardsHistory.containsKey(dataDate))
            {
                String[] split = mainActivity.dataSaver.loadedData.studiedTimeHistory.get(dataDate).split(":");
                float time = (float) (Integer.parseInt(split[0]) * 60 + Integer.parseInt(split[1]) + Integer.parseInt(split[2]) / 60.0);
                reviewTimeData.add(new Entry(index, time));
            }
            else
                reviewTimeData.add(new Entry(index, 0));
        }

        String[] split = mainActivity.dailyReview.totalSpentTimeStudying.split(":");
        float time = (float) (Integer.parseInt(split[0]) * 60 + Integer.parseInt(split[1]) + Integer.parseInt(split[2]) / 60.0);
        reviewTimeData.add(new Entry(historyOffset, time));

        LineDataSet set2 = new LineDataSet(reviewTimeData, "Review Time Minutes");
        set2.setValueTextColor(Color.WHITE);
        set2.setColor(Color.GREEN);
        set2.setDrawCircles(false);

        timeReviewLineChart.getLegend().setTextColor(Color.WHITE);
        timeReviewLineChart.getAxisLeft().setTextColor(Color.WHITE);
        timeReviewLineChart.getAxisRight().setTextColor(Color.WHITE);
        timeReviewLineChart.getXAxis().setDrawLabels(false);
        timeReviewLineChart.getLegend().setTextColor(Color.WHITE);
        timeReviewLineChart.getDescription().setEnabled(false);

        LineData data = new LineData( set2);
        timeReviewLineChart.setData(data);

        timeReviewLineChart.animateX(1000);
    }
}
