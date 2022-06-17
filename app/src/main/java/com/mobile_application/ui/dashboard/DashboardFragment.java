package com.mobile_application.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile_application.AddList;
import com.mobile_application.Home;
import com.mobile_application.MainActivity;
import com.mobile_application.R;
import com.mobile_application.utils.LocalDb;
import com.mobile_application.utils.UserDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
    private DashboardViewModel mViewModel;
    private ListView mlistView;
    private Button mButtonAddList;
    private List<List<String>> items = new ArrayList<>();
    private String myAccount;
    private int connectFlag;
    private ListAdapter adapter;
    private View view;

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        Intent intent = ((AppCompatActivity) getActivity()).getIntent();
        connectFlag = Integer.valueOf(intent.getStringExtra("connectFlag")).intValue();
        myAccount = intent.getStringExtra("myAccount").toString();

        LocalDb localDb = new LocalDb((AppCompatActivity) getActivity(), "app.db", null, 1, myAccount + "_list");
        SQLiteDatabase sqliteDatabase = localDb.getWritableDatabase();

        UserDAO userDao = new UserDAO();
        try {
            userDao.synchronizeListItem(sqliteDatabase, myAccount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<List<String>> selectRes = new ArrayList<>();
        try {
            selectRes = userDao.selectListItem(sqliteDatabase, myAccount);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        if(selectRes != null) {
            items = selectRes;
        }

        adapter = new ListAdapter((AppCompatActivity)getActivity(), items);
        mlistView = (ListView) view.findViewById(R.id.toDoList);
        mlistView.setAdapter(adapter);

        mButtonAddList = (Button) view.findViewById(R.id.buttonAddList);
        mButtonAddList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent((AppCompatActivity)getActivity(), AddList.class);
                intent.putExtra("myAccount", myAccount);
                startActivity(intent);
            }
        });

        return view;
    }

    class ListAdapter extends BaseAdapter {
        Context mcontext;
        List<List<String>> arrayList;
        LayoutInflater inflater;

        public ListAdapter(Context context, List<List<String>> list) {
            this.mcontext = context;
            this.arrayList = list;
            inflater = LayoutInflater.from(mcontext);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return (arrayList.get(position)).get(0);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.list_item, null);
                holder.mTextView = (TextView) convertView.findViewById(R.id.itemName);
                holder.mCheckBox = (CheckBox) convertView.findViewById(R.id.checkDone);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.mTextView.setText((arrayList.get(position)).get(0).toString());
            holder.mCheckBox.setTag(position);
            holder.mTextView.setTag(position);

            holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.itemName:{
                            Toast.makeText((AppCompatActivity)getActivity(), "该任务完成时间需要" + arrayList.get(position).get(1) + "分钟", Toast.LENGTH_LONG).show();
                            break;
                        }
                        case R.id.checkDone:{
                            String content = getItem(position).toString();
                            String studyTime = arrayList.get(position).get(1);
                            holder.mCheckBox.setChecked(false);
                            arrayList.remove(position);
                            notifyDataSetChanged();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        UserDAO userDao = new UserDAO();
                                        LocalDb localDb = new LocalDb((AppCompatActivity) getActivity(), "app.db", null, 1, myAccount + "_list");
                                        Time time = new Time();
                                        time.setToNow();
                                        String curDate = String.valueOf(time.year) + "-" + String.valueOf(time.month + 1) + "-" + String.valueOf(time.monthDay);
                                        String insertTime = String.valueOf(time.year) + "-" + String.valueOf(time.month + 1) + "-" + String.valueOf(time.monthDay) + " " + String.valueOf(time.hour) + ":" + String.valueOf(time.minute) + ":" + String.valueOf(time.second);
                                        SQLiteDatabase sqliteDatabase = localDb.getWritableDatabase();
                                        userDao.deleteListItem(sqliteDatabase, myAccount, content, curDate, studyTime, insertTime);
                                        userDao.deleteRemoteListItem(myAccount, content);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                            break;
                        }
                    }
                }
            });
            holder.mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.itemName:{
                            Toast.makeText((AppCompatActivity)getActivity(), "该任务完成时间需要" + arrayList.get(position).get(1) + "分钟", Toast.LENGTH_LONG).show();
                            break;
                        }
                        case R.id.checkDone:{
                            String content = getItem(position).toString();
                            String studyTime = arrayList.get(position).get(1);
                            holder.mCheckBox.setChecked(false);
                            arrayList.remove(position);
                            notifyDataSetChanged();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        UserDAO userDao = new UserDAO();
                                        LocalDb localDb = new LocalDb((AppCompatActivity) getActivity(), "app.db", null, 1, myAccount + "_list");
                                        Time time = new Time();
                                        time.setToNow();
                                        String curDate = String.valueOf(time.year) + "-" + String.valueOf(time.month + 1) + "-" + String.valueOf(time.monthDay);
                                        String insertTime = String.valueOf(time.year) + "-" + String.valueOf(time.month + 1) + "-" + String.valueOf(time.monthDay) + " " + String.valueOf(time.hour) + ":" + String.valueOf(time.minute) + ":" + String.valueOf(time.second);
                                        SQLiteDatabase sqliteDatabase = localDb.getWritableDatabase();
                                        userDao.deleteListItem(sqliteDatabase, myAccount, content, curDate, studyTime, insertTime);
                                        userDao.deleteRemoteListItem(myAccount, content);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                            break;
                        }
                    }
                }
            });

            return convertView;
        }

        class ViewHolder {
            TextView mTextView;
            CheckBox mCheckBox;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        // TODO: Use the ViewModel

    }

    @Override
    public void onResume() {
        super.onResume();
        LocalDb localDb = new LocalDb((AppCompatActivity) getActivity(), "app.db", null, 1, myAccount + "_list");
        SQLiteDatabase sqliteDatabase = localDb.getWritableDatabase();

        UserDAO userDao = new UserDAO();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    userDao.synchronizeListItem(sqliteDatabase, myAccount);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<List<String>> selectRes = new ArrayList<>();
        try {
            selectRes = userDao.selectListItem(sqliteDatabase, myAccount);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        items.clear();

        if (selectRes != null) {
            items = selectRes;
        }
        adapter = new ListAdapter((AppCompatActivity)getActivity(), items);
        mlistView = (ListView) view.findViewById(R.id.toDoList);
        mlistView.setAdapter(adapter);
    }
}