package com.mobile_application.ui.settings;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mobile_application.Config;
import com.mobile_application.databinding.FragmentSettingsBinding;

import java.util.HashMap;
import java.util.Map;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding viewBinding;
    private String myAccount;
    final static int FILE_REQUEST_CODE = 10086;
    private static final String TAG = "SettingsFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        viewBinding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = viewBinding.getRoot();

        SharedPreferences preferences = Config.getConfig((AppCompatActivity) getActivity());
        viewBinding.editList1.setText(preferences.getString("synchronizeTime", "60"));

        Intent intent = ((AppCompatActivity) getActivity()).getIntent();
        myAccount = intent.getStringExtra("myAccount");
        viewBinding.textName.setText(myAccount);

        viewBinding.buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String synchronizeTime = viewBinding.editList1.getText().toString();

                Map<String, String> map = new HashMap<String, String>();
                map.put("synchronizeTime", synchronizeTime);

                Config.setConfig((AppCompatActivity)getActivity(), map);
            }
        });

        viewBinding.buttonChangeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, FILE_REQUEST_CODE);
            }
        });

        final TextView textView = viewBinding.textSettings;
        settingsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewBinding = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if(data.getData() != null) {
                Uri uri = data.getData();
                String path = uri.getPath();
                Log.d(TAG, path);
            }
        }
    }
}