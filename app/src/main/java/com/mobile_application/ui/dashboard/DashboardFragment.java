package com.mobile_application.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
    private  List<String> items = new ArrayList<>();
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
        List<List<String>> selectRes = new ArrayList<>();
        try {
            selectRes = userDao.selectListItem(sqliteDatabase, myAccount);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        if(selectRes != null) {
            for(int i = 0;i < selectRes.size();i++) {
                List<String> temp = selectRes.get(i);
                items.add(temp.get(0));
            }
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
        List<String> arrayList;
        LayoutInflater inflater;

        public ListAdapter(Context context, List<String> list) {
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
            return arrayList.get(position);
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

            holder.mTextView.setText(arrayList.get(position).toString());
            //holder.mCheckBox.setTag(position);

            holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String content = getItem(position).toString();
                    holder.mCheckBox.setChecked(false);
                    arrayList.remove(position);
                    notifyDataSetChanged();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                UserDAO userDao = new UserDAO();
                                LocalDb localDb = new LocalDb((AppCompatActivity) getActivity(), "app.db", null, 1, myAccount + "_list");
                                SQLiteDatabase sqliteDatabase = localDb.getWritableDatabase();
                                userDao.updateListItem(sqliteDatabase, myAccount, content);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
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
        List<List<String>> selectRes = new ArrayList<>();
        try {
            selectRes = userDao.selectListItem(sqliteDatabase, myAccount);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        items.clear();

        if (selectRes != null) {
            for (int i = 0; i < selectRes.size(); i++) {
                List<String> temp = selectRes.get(i);
                items.add(temp.get(0));
            }
        }
        adapter = new ListAdapter((AppCompatActivity)getActivity(), items);
        mlistView = (ListView) view.findViewById(R.id.toDoList);
        mlistView.setAdapter(adapter);
    }
}