package com.example.japotimeapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.japotimeapp.MainActivity;
import com.example.japotimeapp.R;

public class MainPageFragment extends Fragment
{
    private MainActivity mainActivity;

    public MainPageFragment(MainActivity _mainActivity)
    {
        mainActivity = _mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.main_page, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        TextView newCards = view.findViewById(R.id.mainPageNewCards);
        TextView refreshCards = view.findViewById(R.id.mainPageRefreshCards);
        TextView reviewCards = view.findViewById(R.id.mainPageReviewCards);
        TextView lastCheckCards = view.findViewById(R.id.mainPageLastCheckCards);

        newCards.setText("" + mainActivity.dailyReview.GetNewCardsCount());
        refreshCards.setText("" + mainActivity.dailyReview.GetRefreshCardsCount());
        reviewCards.setText("" + mainActivity.dailyReview.GetInReviewCardsCount());
        lastCheckCards.setText("" + mainActivity.dailyReview.GetLastCheckCardsCount());

        Button studyBtn = view.findViewById(R.id.mainPageStudyBtn);
        studyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new StudyPageFragment(mainActivity)).commit();
            }
        });
    }
}
