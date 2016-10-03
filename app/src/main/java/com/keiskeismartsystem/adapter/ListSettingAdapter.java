package com.keiskeismartsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.keiskeismartsystem.R;

import org.w3c.dom.Text;

/**
 * Created by zeta on 10/18/2015.
 */
public class ListSettingAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    public ListSettingAdapter(Context context, String[] values){
        super(context, R.layout.list_setting, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = layoutInflater.inflate(R.layout.list_setting, parent, false);
        TextView tv_title = (TextView) rootView.findViewById(R.id.tv_title);
        tv_title.setText(values[position]);
        return rootView;
    }
}
