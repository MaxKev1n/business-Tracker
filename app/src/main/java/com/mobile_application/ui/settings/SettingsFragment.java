package com.mobile_application.ui.settings;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mobile_application.Config;
import com.mobile_application.Home;
import com.mobile_application.MainActivity;
import com.mobile_application.R;
import com.mobile_application.databinding.FragmentSettingsBinding;
import com.mobile_application.utils.UserDAO;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding viewBinding;
    private String myAccount = "default";
    private int connectFlag = 0;
    final static int FILE_REQUEST_CODE = 10086;
    private static final String TAG = "SettingsFragment";
    private Bitmap curBitmap;

    private String searchResString = null;
    private boolean searchRes = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        viewBinding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = viewBinding.getRoot();

        SharedPreferences preferences = Config.getConfig((AppCompatActivity) getActivity());
        viewBinding.editList1.setText(preferences.getString("synchronizeTime", "60"));

        Intent intent = ((AppCompatActivity) getActivity()).getIntent();
        connectFlag = Integer.valueOf(intent.getStringExtra("connectFlag"));
        myAccount = intent.getStringExtra("myAccount").toString();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UserDAO userDAO = new UserDAO();
                    Blob remoteBlob = userDAO.selectUserImg(myAccount);
                    if(remoteBlob != null) {
                        byte[] bytes = remoteBlob.getBytes(1, (int)remoteBlob.length());
                        curBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        String savePath = getActivity().getFilesDir()+ "/imgs/";
                        Log.d(TAG, savePath);
                        File dir = new File(savePath);
                        if(!dir.exists()) {
                            dir.mkdir();
                        }
                        File saveFile = new File(savePath, "userImg.jpeg");
                        saveFile.createNewFile();
                        FileOutputStream saveImg = new FileOutputStream(saveFile);
                        curBitmap.compress(Bitmap.CompressFormat.JPEG, 80, saveImg);
                        saveImg.flush();
                        saveImg.close();
                    }
                    else {
                        File userImg = new File(getActivity().getFilesDir()+ "/imgs/", "userImg.jpeg");
                        if(userImg.exists()) {
                            curBitmap = BitmapFactory.decodeFile(getActivity().getFilesDir()+ "/imgs/userImg.jpeg");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if(connectFlag == 1) {
            thread.start();
        }
        else {
            File userImg = new File(getActivity().getFilesDir()+ "/imgs/", "userImg.jpeg");
            if(userImg.exists()) {
                curBitmap = BitmapFactory.decodeFile(getActivity().getFilesDir()+ "/imgs/userImg.jpeg");
            }
        }
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        viewBinding.userImg.setImageBitmap(curBitmap);
        viewBinding.textName.setText(myAccount);

        viewBinding.buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String synchronizeTime = viewBinding.editList1.getText().toString();

                Map<String, String> map = new HashMap<String, String>();
                map.put("synchronizeTime", synchronizeTime);

                Config.setConfig((AppCompatActivity)getActivity(), map);

                //upload user image
                if(connectFlag == 1) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            UserDAO userDAO = new UserDAO();
                            try {
                                Bitmap bitmap = ((BitmapDrawable)viewBinding.userImg.getDrawable()).getBitmap();
                                userDAO.updateUserImg(myAccount, bitmap);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
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

        viewBinding.buttonWebRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://github.com/MaxKev1n/mobile_application");
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setClassName("com.android.chrome","com.google.android.apps.chrome.Main");
                    startActivity(intent);
                } catch (Exception e) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });

        viewBinding.buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Config.clear((AppCompatActivity)getActivity());
                Intent intent = new Intent((AppCompatActivity)getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        viewBinding.buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userAccount = viewBinding.editText3.getText().toString();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UserDAO userDAO = new UserDAO();
                        List<String> res = null;
                        try {
                            res = userDAO.selectOtherData(userAccount);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        if(res != null) {
                            searchResString = "该用户累计任务"+ res.get(0) + "项，累计时间" + res.get(1) + "分钟";
                            searchRes = true;
                        }
                        else {
                            searchRes = false;
                        }
                    }
                });
                thread.start();
                try {
                    thread.join();
                    if(searchRes) {
                        Toast.makeText((AppCompatActivity)getActivity(), searchResString, Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText((AppCompatActivity)getActivity(), "不存在该用户", Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
                    String savePath = getActivity().getFilesDir()+ "/imgs/";
                    Log.d(TAG, savePath);
                    File dir = new File(savePath);
                    if(!dir.exists()) {
                        dir.mkdir();
                    }
                    File saveFile = new File(savePath, "userImg.jpeg");
                    saveFile.createNewFile();
                    FileOutputStream saveImg = new FileOutputStream(saveFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, saveImg);
                    saveImg.flush();
                    saveImg.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                viewBinding.userImg.setImageURI(uri);
            }
        }
    }
}