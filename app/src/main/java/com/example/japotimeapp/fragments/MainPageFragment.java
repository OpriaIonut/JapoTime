package com.example.japotimeapp.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.japotimeapp.MainActivity;
import com.example.japotimeapp.R;
import com.example.japotimeapp.enums.ActiveFragment;

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

    @SuppressLint("SetTextI18n")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        TextView newCards = view.findViewById(R.id.mainPageNewCards);
        TextView refreshCards = view.findViewById(R.id.mainPageRefreshCards);
        TextView reviewCards = view.findViewById(R.id.mainPageReviewCards);
        TextView lastCheckCards = view.findViewById(R.id.mainPageLastCheckCards);

        TextView cardsStudiedToday = view.findViewById(R.id.mainPageCardsStudied);
        cardsStudiedToday.setText("Cards studied today: " + mainActivity.dailyReview.cardsStudiedToday);

        TextView mainPageStudyTime = view.findViewById(R.id.mainPageStudyTime);
        mainPageStudyTime.setText("Time spent studying: " + mainActivity.dailyReview.totalSpentTimeStudying);

        newCards.setText("" + mainActivity.dailyReview.GetNewCardsCount());
        refreshCards.setText("" + mainActivity.dailyReview.GetRefreshCardsCount());
        reviewCards.setText("" + mainActivity.dailyReview.GetInReviewCardsCount());
        lastCheckCards.setText("" + mainActivity.dailyReview.GetLastCheckCardsCount());

        Button studyBtn = view.findViewById(R.id.mainPageStudyBtn);
        studyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mainActivity.dailyReview.inReviewIDs.size() == 0 && mainActivity.dailyReview.refreshCardsIDs.size() == 0 && mainActivity.dailyReview.newCardsIDs.size() == 0 && mainActivity.dailyReview.lastCheckIDs.size() == 0)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                    builder.setTitle(R.string.app_name);
                    builder.setMessage("Do you want to start a new deck?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        public void onClick(DialogInterface dialog, int id) {
                            mainActivity.isStudyPageActive = true;
                            mainActivity.dailyReview.GenerateDayReview();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new StudyPageFragment(mainActivity)).commit();
                            mainActivity.currentActiveFragment = ActiveFragment.StudyPage;
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else
                {
                    mainActivity.isStudyPageActive = true;
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new StudyPageFragment(mainActivity)).commit();
                    mainActivity.currentActiveFragment = ActiveFragment.StudyPage;
                }
            }
        });

        ImageButton settingsBtn = view.findViewById((R.id.mainPageSettingsBtn));
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new SettingsPageFragment(mainActivity)).commit();
                mainActivity.currentActiveFragment = ActiveFragment.SettingsPage;
            }
        });
    }
}
