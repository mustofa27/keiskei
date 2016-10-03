package com.keiskeismartsystem.fragment;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.keiskeismartsystem.LoginActivity;
import com.keiskeismartsystem.MainActivity;
import com.keiskeismartsystem.R;
import com.keiskeismartsystem.adapter.ListSettingAdapter;
import com.keiskeismartsystem.dbsql.ChatTransact;
import com.keiskeismartsystem.dbsql.NotifTransact;
import com.keiskeismartsystem.helper.UserSession;

public class SettingFragment extends Fragment {
    private static ListView _listView;
    private static View _rootView;
    private static UserSession _us;
    private static NotifTransact _notifTransact;
    private static ChatTransact _chatTransact;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _us = new UserSession(getActivity().getApplicationContext());
        _notifTransact = new NotifTransact(getActivity().getApplicationContext());
        _chatTransact = new ChatTransact(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        _listView = (ListView) _rootView.findViewById(R.id.lv_setting);
        String[] values = new String[] {"About", "Profile", "Notification", "Logout"};
        ListSettingAdapter adapter = new ListSettingAdapter(getActivity(), values);
        _listView.setAdapter(adapter);
        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);

                if (item.equals("Logout")) {
                    _us.logout();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    _notifTransact.truncate();
                    _chatTransact.truncate();
                    startActivity(intent);
                    getActivity().finish();
                } else if (item.equals("About")) {
                    Bundle bundle = new Bundle();
                    ((MainActivity) getActivity()).changeFragment("about", _rootView, bundle);
                }else if (item.equals("Notification")) {
                    Bundle bundle = new Bundle();
                    ((MainActivity) getActivity()).changeFragment("notification", _rootView, bundle);
                }else if (item.equals("Profile")) {
                    Bundle bundle = new Bundle();
                    ((MainActivity) getActivity()).changeFragment("profile_fragment", _rootView, bundle);
                }
            }
        });
        return _rootView;
    }

}
