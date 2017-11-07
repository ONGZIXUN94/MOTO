package com.example.dqw648.moto;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by PMXT36 on 11/7/2017.
 */

public class ScanMsgSvc extends IntentService {

    public static final String PARAM_IN_MSG = "input";
    public static final String PARAM_OUT_MSG = "output";

    public ScanMsgSvc() {
        super(ScanMsgSvc.class.getName());
    }

    private boolean m_quit = false;

    @Override
    public void onDestroy() {
        m_quit = true;
        super.onDestroy();

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String dataString = intent.getStringExtra(PARAM_IN_MSG);


        while(ZelloWrapper.checkTriggerToJoinGroup(dataString) !=true && !m_quit){
            // Log.d("svc", String.format("checking for %s", dataString));
        }

        if (!m_quit){
            Log.d("svc", "done check trigger");
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(MainActivity.ResponseReceiver.ACTION_RESP);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(PARAM_OUT_MSG, dataString);
            sendBroadcast(broadcastIntent);
        }else{
            Log.d("svc", "active device.. quit scan");
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(MainActivity.ResponseReceiver.ACTION_RESP);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(PARAM_OUT_MSG, "");
            sendBroadcast(broadcastIntent);
        }


    }
}
