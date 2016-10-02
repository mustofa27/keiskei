package com.alvarisi.keiskeismartsystem.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alvarisi.keiskeismartsystem.R;
import com.alvarisi.keiskeismartsystem.model.Notif;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by zeta on 11/2/2015.
 */
public class NotificationAdapter extends ArrayAdapter<Notif> {
    private static class ViewHolder {
        TextView title;
        LinearLayout bgrnd;
    }
    private Context context;
    public NotificationAdapter(Context context, ArrayList<Notif> notifies){
        super(context, 0, notifies);
        this.context = context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Notif notif = getItem(position);
        if (convertView == null){

            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_notifies, parent, false);

            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.bgrnd = (LinearLayout) convertView.findViewById(R.id.bg);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if((position%2) == 1){
            viewHolder.bgrnd.setBackgroundColor(Color.parseColor("#b8c6ab"));

        }else{
            viewHolder.bgrnd.setBackgroundColor(Color.parseColor("#e9ecd9"));
        }
        viewHolder.title.setTextColor(Color.parseColor("#0d3323"));
        if(notif.getTitle().length() > 31){
            viewHolder.title.setText(notif.getTitle().substring(0, 30));
        }else{
            viewHolder.title.setText(notif.getTitle());
        }

        return convertView;
    }
}
