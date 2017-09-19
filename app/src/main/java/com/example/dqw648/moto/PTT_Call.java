package com.example.dqw648.moto;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.zello.sdk.Zello;

public class PTT_Call extends AppCompatActivity {

    //String
    String detected_username;
    String tag = "zello";

    /*
    *
    * Sample to broadcast to everyone
    * Zello.getInstance().setSelectedChannelOrGroup("Everyone");
    *
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptt__call);
        Bundle from_listview = getIntent().getExtras();
        detected_username = from_listview.getString("username");
    }

    public void ptt_init(){
        Zello.getInstance().configure("net.loudtalks", this);
    }

    public void ptt_start(String name){
        if (name != "") {
            Zello.getInstance().setSelectedUserOrGateway(name);
            Log.d(tag, String.format("start ptt call to %s\r\n", name));
            Zello.getInstance().beginMessage();
        }
        else{
            Log.e(tag, "username is empty");
        }

    }

    public void ptt_stop(){
        Zello.getInstance().endMessage();
        Log.d(tag, "stop ptt call\r\n");
    }
}
