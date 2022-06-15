package com.mobile_application.ui.dashboard;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

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

        mlistView = (ListView) view.findViewById(R.id.toDoList);
        mlistView.setAdapter(new ListAdapter());

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

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position).toString();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view=View.inflate((AppCompatActivity)getActivity(),R.layout.list_item,null);
            TextView mTextView=(TextView) view.findViewById(R.id.itemName);
            mTextView.setText(items.get(position).toString());
            return view;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        // TODO: Use the ViewModel

    }
}