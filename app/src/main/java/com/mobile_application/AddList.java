package com.mobile_application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import com.mobile_application.databinding.ActivityAddListBinding;
import com.mobile_application.databinding.ActivityMainBinding;
import com.mobile_application.utils.LocalDb;
import com.mobile_application.utils.UserDAO;

import java.sql.SQLException;

public class AddList extends AppCompatActivity {
    private ActivityAddListBinding viewBinding;
    private String myAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityAddListBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        Intent intent = this.getIntent();
        myAccount = intent.getStringExtra("myAccount").toString();

        viewBinding.buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String content = viewBinding.textContent.getText().toString();
                        String time = viewBinding.textTime.getText().toString();
                        LocalDb localDb = new LocalDb(AddList.this, "app.db", null, 1, myAccount + "_list");
                        SQLiteDatabase sqliteDatabase = localDb.getWritableDatabase();
                        UserDAO userDao = new UserDAO();
                        try {
                            userDao.insertListItem(sqliteDatabase, myAccount, content, time);
                            userDao.insertRemoteListItem(myAccount, content, time);
                            userDao.synchronizeListItem(sqliteDatabase, myAccount);
                        } catch (Exception throwables) {
                            throwables.printStackTrace();
                        }
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
    }
}