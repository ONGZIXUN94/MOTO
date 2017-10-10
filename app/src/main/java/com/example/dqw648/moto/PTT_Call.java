package com.example.dqw648.moto;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zello.sdk.Zello;

public class PTT_Call extends AppCompatActivity {

    //String
    String detected_username;
    String tag = "zello";

    /*
    * NOTE: You need to have zello work app (not the one from appstore) installed using this link
    * http://mugun88.zellowork.com/app
    *
    * Sample to broadcast to everyone
    * Zello.getInstance().setSelectedChannelOrGroup("Everyone");
    *
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptt__call);

        TextView tv_callname = (TextView) findViewById(R.id.tv_callname);
        ptt_init();

        Bundle from_listview = getIntent().getExtras();
        detected_username = from_listview.getString("username");

        tv_callname.setText(detected_username);

        Button pttButton = (Button) findViewById(R.id.pttButton);

        pttButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                int action = motionEvent.getAction();
                if (action == MotionEvent. ACTION_DOWN ) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Transmitting...", Toast.LENGTH_SHORT);
                    toast.show();
                    ptt_start(detected_username);
                } else if (action == MotionEvent. ACTION_UP || action == MotionEvent. ACTION_CANCEL ) {
                    ptt_stop();
                    Toast toast = Toast.makeText(getApplicationContext(), "Voice message sent.", Toast.LENGTH_SHORT);
                    toast.show();
                }
                return false;
            }
        });
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
