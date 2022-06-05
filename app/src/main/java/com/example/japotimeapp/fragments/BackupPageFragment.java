package com.example.japotimeapp.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.japotimeapp.MainActivity;
import com.example.japotimeapp.R;
import com.example.japotimeapp.enums.ActiveFragment;
import com.example.japotimeapp.utils.DailyReview;
import com.example.japotimeapp.utils.KanjiCollection;
import com.google.android.material.textfield.TextInputLayout;

public class BackupPageFragment extends Fragment
{
    private final MainActivity mainActivity;

    private TextInputLayout backupSaveFileField;

    public BackupPageFragment(MainActivity _mainActivity)
    {
        mainActivity = _mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.backup_page, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        backupSaveFileField = view.findViewById(R.id.backupFileName);

        if(mainActivity.dataSaver.loadedData != null && mainActivity.dataSaver.loadedData.onlineSaveDataFile != null)
        {
            backupSaveFileField.getEditText().setText(mainActivity.dataSaver.loadedData.onlineSaveDataFile);
        }

        Button backBtn = view.findViewById(R.id.backupPageBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new SettingsPageFragment(mainActivity)).commit();
                mainActivity.currentActiveFragment = ActiveFragment.SettingsPage;
            }
        });

        Button uploadBtn = view.findViewById(R.id.backupSaveBtn);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = backupSaveFileField.getEditText().getText().toString();
                if(fileName.length() == 0)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                    builder.setTitle(R.string.app_name);
                    builder.setMessage("File name cannot be empty");
                    builder.setPositiveButton("Close", (dialog, id) -> dialog.dismiss());
                    AlertDialog alert = builder.create();
                    alert.show();
                    return;
                }

                if(ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    mainActivity.dataSaver.SaveDataOnline(fileName, mainActivity.currentDate);
                }
                else
                {
                    ActivityCompat.requestPermissions(mainActivity, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, 3);
                }
            }
        });

        Button downloadBtn = view.findViewById(R.id.backupDownloadBtn);
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = backupSaveFileField.getEditText().getText().toString();
                if(fileName.length() == 0)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                    builder.setTitle(R.string.app_name);
                    builder.setMessage("File name cannot be empty");
                    builder.setPositiveButton("Close", (dialog, id) -> dialog.dismiss());
                    AlertDialog alert = builder.create();
                    alert.show();
                    return;
                }

                if(ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                    builder.setTitle(R.string.app_name);
                    builder.setMessage("Are you sure you want to download the backup? This will overwrite all your data.");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        public void onClick(DialogInterface dialog, int id) {
                            mainActivity.dataSaver.LoadDataOnline(fileName, mainActivity.currentDate);
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
                    ActivityCompat.requestPermissions(mainActivity, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, 3);
                }
            }
        });
    }
}
