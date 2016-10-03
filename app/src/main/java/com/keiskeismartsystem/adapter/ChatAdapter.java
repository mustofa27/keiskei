package com.keiskeismartsystem.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.keiskeismartsystem.R;
import com.keiskeismartsystem.helper.ImageHelper;
import com.keiskeismartsystem.model.Chat;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * keiskei smartsystem_V2.
 */
public class ChatAdapter extends ArrayAdapter<Chat> {
    public static final String _base_url = "https://www.keiskei.co.id/";
    private final ArrayList<Chat> chats;
    private Activity context;
    private static class ViewHolder {
        TextView left;
        TextView description;
        TextView right;
        ImageView photo;
        LinearLayout content;
        LinearLayout content_bg;

    }
    public ChatAdapter(Activity context, ArrayList<Chat> chats){
        super(context, 0, chats);
        this.context = context;
        this.chats = chats;
    }
    @Override
    public int getCount() {
        if (chats != null){
            return chats.size();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Chat chat = getItem(position);

        if (convertView == null){
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(this.context).inflate(R.layout.list_chat, parent, false);

            viewHolder.description = (TextView) convertView.findViewById(R.id.tv_description);
            viewHolder.left = (TextView) convertView.findViewById(R.id.tv_left);
            viewHolder.right  = (TextView) convertView.findViewById(R.id.tv_right);
            viewHolder.photo  = (ImageView) convertView.findViewById(R.id.iv_chat);
            viewHolder.content = (LinearLayout) convertView.findViewById(R.id.ll_content);
            viewHolder.content_bg = (LinearLayout) convertView.findViewById(R.id.ll_content_bg);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
//        Log.v("keiskeidebug assd", chat.getName() + chat.getDescription() + chat.getIsAdmin() + chat.getReadFlag() + chat.getPhotoExt() + chat.getPhotoInt());
        char is_admin = chat.getIsAdmin();
//        char is_admin = '0';
        if(is_admin == '0') {
           viewHolder.content_bg.setBackgroundResource(R.drawable.bg_chat_a);
           LinearLayout.LayoutParams layoutParams =
                   (LinearLayout.LayoutParams) viewHolder.content_bg.getLayoutParams();
           layoutParams.gravity = Gravity.RIGHT;
           viewHolder.content_bg.setLayoutParams(layoutParams);
           RelativeLayout.LayoutParams lp =
                   (RelativeLayout.LayoutParams) viewHolder.content.getLayoutParams();
           lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
           lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
           viewHolder.content.setLayoutParams(lp);
           layoutParams = (LinearLayout.LayoutParams) viewHolder.description.getLayoutParams();
           layoutParams.gravity = Gravity.RIGHT;
           viewHolder.description.setLayoutParams(layoutParams);


            viewHolder.left.setTextAppearance(context, R.style.boldText);
            viewHolder.right.setTextAppearance(context, R.style.normalText);
            viewHolder.left.setText(chat.getName());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = chat.getCreatedAt();
            String temp = "";
            if (date != null){
                temp = format.format(date);
            }
            viewHolder.right.setText(temp);
        }else {
           viewHolder.content_bg.setBackgroundResource(R.drawable.bg_chat_b);
           LinearLayout.LayoutParams layoutParams =
                   (LinearLayout.LayoutParams) viewHolder.content_bg.getLayoutParams();
           layoutParams.gravity = Gravity.LEFT;
           viewHolder.content_bg.setLayoutParams(layoutParams);
           RelativeLayout.LayoutParams lp =
                   (RelativeLayout.LayoutParams) viewHolder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
           lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

           viewHolder.content.setLayoutParams(lp);
           layoutParams = (LinearLayout.LayoutParams) viewHolder.description.getLayoutParams();
           layoutParams.gravity = Gravity.LEFT;
           viewHolder.description.setLayoutParams(layoutParams);
            viewHolder.left.setTextAppearance(context, R.style.normalText);
            viewHolder.right.setTextAppearance(context, R.style.boldText);
            viewHolder.right.setText(chat.getName());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = chat.getCreatedAt();
            String temp = "";
            if (date != null){
                temp = format.format(date);
            }
            viewHolder.left.setText(temp);
       }
        String temp_t = chat.getPhotoExt();
        if (temp_t != null || !temp_t.isEmpty()){
            viewHolder.photo.setVisibility(View.VISIBLE);
            Picasso.with(context).load(_base_url + "ws/data/chat_mobile/" + temp_t)
                    .into(viewHolder.photo);
        }else{
            viewHolder.photo.setVisibility(View.INVISIBLE);
        }
//        if (temp != null || !temp.isEmpty()){
//            File file = new File(temp);
//            if (file.exists()){
//                BitmapFactory.Options bmo = new BitmapFactory.Options();
//                try {
//                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmo);
//                    viewHolder.photo.setImageBitmap(bitmap);
//                }catch (OutOfMemoryError e){
//                    e.printStackTrace();
//                }
//            }if (temp_t != null || !temp_t.isEmpty()) {
////                new LoadImage().execute(temp_t);
//                viewHolder.photo.setVisibility(View.INVISIBLE);
//            }
//        }else /* if (temp_t != null || !temp_t.isEmpty()) */{
//            viewHolder.photo.setVisibility(View.INVISIBLE);
//        }

        viewHolder.description.setText(chat.getDescription());

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Chat getItem(int position) {
        if (chats != null) {
            return chats.get(position);
        } else {
            return null;
        }

    }
    public void add(Chat chat){
        chats.add(chat);
    }

//    private class LoadImage extends AsyncTask<String, String, Bitmap> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//        protected Bitmap doInBackground(String... args) {
//
//            try {
//                Log.v("keiskeidebug", "https://www.keiskei.co.id/data/chat_mobile/" + args[0]);
//                bitmap_t = BitmapFactory.decodeStream((InputStream)new URL("https://www.keiskei.co.id/data/chat_mobile/" + args[0]).getContent());
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return bitmap_t;
//        }
//
//        protected void onPostExecute(Bitmap image) {
//            if(image != null){
////                ImageHelper ih = new ImageHelper();
//                Bitmap icon = ih.getRoundedCornerBitmap(image, 70);
//                icon = ih.resizeBitmap(icon, 120, 120);
//                icon = ih.getRoundedCornerBitmap(icon, 70);
//                _im_user = (ImageView) _rootView.findViewById(R.id.im_user);
//                _im_user.setImageBitmap(icon);
//                String path = ih.storeImage(_context, image);
//                _user.setPathUserInt(path);
//                _us.updateUserSession(_user);
////                icon.recycle();
////                icon = null;
////                image.recycle();
//            }else{
//
//                Toast.makeText(_context, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}
