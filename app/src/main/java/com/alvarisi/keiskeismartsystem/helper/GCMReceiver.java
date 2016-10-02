package com.alvarisi.keiskeismartsystem.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class GCMReceiver extends BroadcastReceiver {
    public GCMReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Bundle bundle= intent.getExtras();
        char flag = bundle.getChar("FLAG");
        Log.v("keiskeidebug", "Receiver FLAG" + flag);
    }
}
