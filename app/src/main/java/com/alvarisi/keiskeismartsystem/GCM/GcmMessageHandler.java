package com.alvarisi.keiskeismartsystem.GCM;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.alvarisi.keiskeismartsystem.MainActivity;
import com.alvarisi.keiskeismartsystem.R;
import com.alvarisi.keiskeismartsystem.dbsql.ChatTransact;
import com.alvarisi.keiskeismartsystem.dbsql.NotifTransact;
import com.alvarisi.keiskeismartsystem.helper.ImageHelper;
import com.alvarisi.keiskeismartsystem.model.Chat;
import com.alvarisi.keiskeismartsystem.model.Notif;
import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zeta on 11/1/2015.
 */
public class GcmMessageHandler extends GcmListenerService {
    public static final int MESSAGE_NOTIFICATION_ID = 435346;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        Bundle bundle = notificationProcess(message);
        Log.v("keiskeidebug", message);

        if(componentInfo.getPackageName().equalsIgnoreCase("com.alvarisi.keiskeismartsystem")){
//            Intent intent = new Intent(this, MainActivity.class);

//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            char flag = bundle.getChar("FLAG");
            if (flag == 'C'){
                Intent intent = new Intent("com.alvarisi.keiskeismartsystem.GCMMESSAGE");
                intent.putExtras(bundle);
                String activityName = componentInfo.getShortClassName();
                if(activityName.equalsIgnoreCase(".chatactivity"))
                {
                    sendBroadcast(intent);
                }
            }else{
                Intent intent = new Intent("com.alvarisi.keiskeismartsystem.GCMMESSAGE.Notification");
                intent.putExtras(bundle);
                createNotification(bundle);
                Log.v("keiskeidebug", "main broadcast scope ");
                String activityName = componentInfo.getShortClassName();
                Log.v("keiskeidebug", "main broadcast scope, actname :" + activityName);
                if(activityName.equalsIgnoreCase(".mainactivity"))
                {
                    Log.v("keiskeidebug", "send broad cast to main");
                    sendBroadcast(intent);
                }

            }


//            startActivity(intent);

//            Intent intent = new Intent("com.innvaderstudio.qcare");
//            intent.putExtras(bundle);
//            sendOrderedBroadcast(intent, null);
        }
        else{

            String title = bundle.getString("title", "");
            createNotification(bundle);

        }
    }


    private void createNotification(Bundle bundle) {
        Context context = getBaseContext();
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.putExtras(bundle);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pintent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(bundle.getString("title"))
                .setContentText("Tap untuk selengkapnya.");
        mBuilder.setContentIntent(pintent);
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }
    private Bundle notificationProcess(String message){
        Bundle bundle_t = new Bundle();
        String title = "NO TITLE";
        Log.v("keiskeidebug", "ntfpro");

        JSONObject json = new JSONObject();
        try {
            json = new JSONObject(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String resp = null;

        try {
            resp = json.getString("RESP");
            Log.v("keiskeidebug","resp " + resp );
            switch (resp){
                case "SCSNTF":
                    bundle_t.putChar("FLAG", 'N');
                    Log.v("keiskeidebug","masuk " + resp );
                    title = "Notifikasi Baru.";
                    String data;
                    data = json.getString("DATA");
                    JSONObject notif_t = new JSONObject(data);
                    Notif notif = new Notif();
                    NotifTransact _notifTransact = new NotifTransact(getBaseContext());
                    try {
                        notif.setSid(Integer.parseInt(notif_t.getString("id")));
                        try {
                            String title_t = notif_t.getString("title");
                            notif.setTitle(title_t);
                        }catch (Exception e){

                        };

                        try {
                            String description_t = notif_t.getString("description");
                            notif.setDescription(description_t);
                        }catch (Exception e){

                        };

                        try {
                            String photo_t = notif_t.getString("photo");
                            notif.setPhotoExt(photo_t);
                        }catch (Exception e){

                        };
                        Log.v("keiskeidebug", notif.getDescription() + notif.getTitle());
                        _notifTransact.insert(notif);
                    }catch (Exception e){
                        Log.v("keiskeidebug", e.toString());
                        e.printStackTrace();
                    }
                    bundle_t.putSerializable(Notif.KEY, notif);
                    break;
                case "SCSCHT":
                    bundle_t.putChar("FLAG", 'C');
                    Log.v("keiskeidebug","masuk " + resp );
                    title = "Chat Baru.";
                    data = json.getString("DATA");
                    JSONObject chat_t = new JSONObject(data);
                    Chat chat = new Chat();
                    ChatTransact _chatTransact = new ChatTransact(getBaseContext());
                    try {

                        chat.setSid(Integer.parseInt(chat_t.getString("id")));
                        try {
                            String name = chat_t.getString("name");
                            chat.setName(name);
                        }catch (Exception e){
                            chat.setName("");
                        };

                        try {
                            String description_t = chat_t.getString("description");
                            chat.setDescription(description_t);
                        }catch (Exception e){
                            chat.setDescription("");
                        };

                        try {
                            String photo_t = chat_t.getString("photo");
                            chat.setPhotoExt(photo_t);
                        }catch (Exception e){
                            chat.setPhotoExt("");
                        };
                        try {
                            String photo_t = chat_t.getString("path_user");
                            chat.setPhotoInt(photo_t);
                        }catch (Exception e){
                            chat.setPhotoInt("");
                        };
                        try {
                            String is_admin_t = chat_t.getString("is_admin");
                            char is_admin_c = is_admin_t.charAt(0);
                            chat.setIsAdmin(is_admin_c);
                        }catch (Exception e){
                            chat.setIsAdmin('0');
                        };
                        try {
                            String is_admin_t = chat_t.getString("read_flag");
                            char is_admin_c = is_admin_t.charAt(0);
                            chat.setReadFlag(is_admin_c);
                        }catch (Exception e){
                            chat.setReadFlag('0');
                        };

                        try {
                            String created_at_t = chat_t.getString("created_at");
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = format.parse(created_at_t);
                            chat.setCreatedAt(date);
                        }catch (ParseException e){

                            Calendar c = Calendar.getInstance();
                            Date date = c.getTime();
                            chat.setCreatedAt(date);
                        }
                        if(chat.getIsAdmin() == '1')
                        {
                            _chatTransact.insert(chat);
                            String photo = null;
                            photo = chat.getPhotoExt();
                            if(photo != null)
                            {
                                LoadImage li = new LoadImage(getBaseContext(), chat);
                                li.execute(chat.getPhotoExt());
                            }

                        }

                    }catch (Exception e){
                        Log.v("keiskeidebug", e.toString());
                        e.printStackTrace();
                    }
                    bundle_t.putSerializable(Chat.KEY, chat);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bundle_t.putBoolean("notif_result", true);
        bundle_t.putString("title", title);
        return  bundle_t;
    }
    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        private Chat chat;
        private Context context;
        public LoadImage(Context context, Chat chat){
            this.chat = chat;
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected Bitmap doInBackground(String... args) {
            Bitmap bitmap_t = null;
            try {
                Log.v("keiskeidebug", "https://www.keiskei.co.id/data/chat_mobile/" + args[0]);
                bitmap_t = BitmapFactory.decodeStream((InputStream) new URL("https://www.keiskei.co.id/data/chat_mobile/" + args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap_t;
        }

        protected void onPostExecute(Bitmap image) {
            if(image != null){
                ImageHelper ih = new ImageHelper();
                String path = ih.storeImage(this.context, image);
                this.chat.setPhotoInt(path);
                ChatTransact _chatTransact = new ChatTransact(getBaseContext());
                _chatTransact.update(this.chat);
            }else{

                Toast.makeText(this.context, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}