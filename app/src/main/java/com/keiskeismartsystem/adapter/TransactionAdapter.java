package com.keiskeismartsystem.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keiskeismartsystem.R;
import com.keiskeismartsystem.model.Transaction;

import java.util.ArrayList;

/**
 * Created by zeta on 11/22/2015.
 */
public class TransactionAdapter extends ArrayAdapter<Transaction> {
    private static class ViewHolder {
        TextView title;
        TextView description;
        LinearLayout bgrnd;
    }
    private Context context;
    public TransactionAdapter(Context context, ArrayList<Transaction> transactionies){
        super(context, 0, transactionies);
        this.context = context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Transaction transaction = getItem(position);
        if (convertView == null){

            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_transaction, parent, false);

            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.description = (TextView) convertView.findViewById(R.id.tv_description);
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
        if(transaction.getCode().length() > 31){
            viewHolder.title.setText(transaction.getCode().substring(0, 30));
        }else{
            viewHolder.title.setText(transaction.getCode());
        }
        String descrption = "Total : Rp. " + transaction.getTotal() + " | Ship : " + transaction.getShip() +
                " | Tgl : " + transaction.getTgl();
        viewHolder.description.setText(descrption);
        return convertView;
    }
}
