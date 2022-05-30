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
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.japotimeapp.MainActivity;
import com.example.japotimeapp.R;
import com.example.japotimeapp.enums.ActiveFragment;

public class SettingsPageFragment extends Fragment
{
    private MainActivity mainActivity;

    private EditText newCardsLimitField;
    private EditText refreshCardsLimitField;
    private EditText inReviewLimitField;

    private ConstraintLayout settingsPageView;
    private ConstraintLayout settingsAlgView;

    private String currentActiveMenu;

    public SettingsPageFragment(MainActivity _mainActivity)
    {
        mainActivity = _mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.settings_page, container, false);
    }

    @SuppressLint("SetTextI18n")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        settingsPageView = view.findViewById(R.id.settingsPageView);
        settingsAlgView = view.findViewById(R.id.settingsAlgView);

        newCardsLimitField = (EditText) view.findViewById(R.id.settingsAlgNewCardsField);
        refreshCardsLimitField = (EditText) view.findViewById(R.id.settingsAlgRefreshCardsField);
        inReviewLimitField = (EditText) view.findViewById(R.id.settingsAlgReviewField);

        newCardsLimitField.setText("" + mainActivity.dailyReview.newCardsLimit);
        refreshCardsLimitField.setText("" + mainActivity.dailyReview.refreshCardsLimit);
        inReviewLimitField.setText("" + mainActivity.dailyReview.inReviewLimit);

        settingsPageView.setVisibility(View.VISIBLE);
        settingsAlgView.setVisibility(View.GONE);
        currentActiveMenu = "MainSettingsPage";

        Button backBtn = view.findViewById(R.id.settingsPageBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if(currentActiveMenu.equals("MainSettingsPage"))
                {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new MainPageFragment(mainActivity)).commit();
                    mainActivity.currentActiveFragment = ActiveFragment.MainPage;
                }
                else if(currentActiveMenu.equals("StudyAlg"))
                {
                    mainActivity.dailyReview.newCardsLimit = Integer.parseInt(newCardsLimitField.getText().toString());
                    mainActivity.dailyReview.refreshCardsLimit = Integer.parseInt(refreshCardsLimitField.getText().toString());
                    mainActivity.dailyReview.inReviewLimit = Integer.parseInt(inReviewLimitField.getText().toString());


                    mainActivity.dailyReview.RecalculateDayReview();
                    currentActiveMenu = "MainSettingsPage";

                    settingsPageView.setVisibility(View.VISIBLE);
                    settingsAlgView.setVisibility(View.GONE);
                }
            }
        });

        Button studyAlgBtn = view.findViewById(R.id.settingsPageStudyAlgBtn);
        studyAlgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentActiveMenu = "StudyAlg";
                settingsPageView.setVisibility(View.GONE);
                settingsAlgView.setVisibility(View.VISIBLE);
            }
        });

        Button statisticsBtn = view.findViewById(R.id.settingsPageStatisticsBtn);
        statisticsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new StatisticsFragment(mainActivity)).commit();
                mainActivity.currentActiveFragment = ActiveFragment.StatisticsPage;
            }
        });

        Button resetProgressBtn = view.findViewById(R.id.settingsPageResetProgressBtn);
        resetProgressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder.setTitle(R.string.app_name);
                builder.setMessage("Are you sure you want to reset all progress");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    public void onClick(DialogInterface dialog, int id) {
                        mainActivity.dailyReview.ResetProgress();
                        mainActivity.dailyReview.RecalculateDayReview();
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
        });

        Button cardBrowserBtn = view.findViewById(R.id.settingsPageCardBrowser);
        cardBrowserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new CardBrowserFragment(mainActivity)).commit();
                mainActivity.currentActiveFragment = ActiveFragment.CardBrowser;
            }
        });
    }
}
