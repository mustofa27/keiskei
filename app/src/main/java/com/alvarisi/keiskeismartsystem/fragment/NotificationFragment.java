package com.alvarisi.keiskeismartsystem.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.alvarisi.keiskeismartsystem.MainActivity;
import com.alvarisi.keiskeismartsystem.R;
import com.alvarisi.keiskeismartsystem.adapter.NotificationAdapter;
import com.alvarisi.keiskeismartsystem.dbsql.NotifTransact;
import com.alvarisi.keiskeismartsystem.dbsql.WhereHelper;
import com.alvarisi.keiskeismartsystem.model.Notif;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {
    private Context _context;
    private NotifTransact _notifTransact;
    private View rootView;
    private static ListView _listview;
    private static AlertDialog alertDialog;
    private NotificationAdapter adapter;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _context = getActivity().getApplicationContext();
        _notifTransact = new NotifTransact(_context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_notification, container, false);
        _listview = (ListView) rootView.findViewById(R.id.lv_notification);
        _listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int total  = _notifTransact.countAll();
                int id_data = total - (int)id;
                Notif notif = (Notif) parent.getItemAtPosition(position);
                notif.setRead("1");
                _notifTransact.update(notif);
                ArrayList<WhereHelper> whDB = new ArrayList<WhereHelper>();
                WhereHelper wh = new WhereHelper("read_flag", "0");
                whDB.add(wh);
                List<Notif> lp = _notifTransact.get(whDB);
                if (lp.size() == 0){
                    ((MainActivity) getActivity()).notifNonActive();
                }else{
                    ((MainActivity) getActivity()).notifActive();
                }
//
//                ArrayList <WhereHelper> where = new ArrayList<WhereHelper>();
//
//                WhereHelper whereHelperDB = new WhereHelper("title", value + "");
//                where.add(whereHelperDB);
//                Notif notif = _notifTransact.first(where);
                Log.v("keiskeidebug", "notif di fg " + id_data + "  " + notif.getTitle());
                Bundle bundle = new Bundle();
                bundle.putSerializable(Notif.KEY, notif);
                ((MainActivity) getActivity()).changeFragment("detail_notif", view, bundle);
            }
        });
        _listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Notif notif = (Notif) parent.getItemAtPosition(position);
                AlertDialog.Builder aBuilder = new AlertDialog.Builder(getActivity());
                String[] choice = {"Hapus"};
                aBuilder.setTitle("Pilihan").setItems(choice, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                ArrayList<WhereHelper> whDB = new ArrayList<WhereHelper>();
                                WhereHelper wh = new WhereHelper("server_id", notif.getSid() + "");
                                whDB.add(wh);
                                _notifTransact.delete(whDB);
                                Toast.makeText(_context, "Notification deleted.", Toast.LENGTH_SHORT).show();
                                adapter.notifyDataSetChanged();
                                _listview.setAdapter(adapter);
                        }
                    }
                });
                alertDialog = aBuilder.create();
                alertDialog.show();
                return false;
            }
        });


        listViewNotif(rootView);

        return rootView;
    }
    public void listViewNotif(View rootView){
        ArrayList<Notif> arrNotif= new ArrayList<Notif>();
        List<Notif> notifies = _notifTransact.all();
        for (Notif notif : notifies){
            arrNotif.add(notif);
        }
        adapter = new NotificationAdapter(_context, arrNotif);

        _listview.setEmptyView(rootView.findViewById(android.R.id.empty));
        _listview.setAdapter(adapter);
    }


}
