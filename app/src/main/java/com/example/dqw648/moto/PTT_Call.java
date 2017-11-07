package com.example.dqw648.moto;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PTT_Call extends AppCompatActivity {

    //String
    String detected_username;
    String detected_callmode;
    int call_mode;

    RelativeLayout layout;

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
        TextView tv_callmode = (TextView) findViewById(R.id.tv_callmode);
        // ZelloWrapper.init(this);
        String dummy = ZelloWrapper.getThisUserName();

        Bundle from_listview = getIntent().getExtras();
        detected_username = from_listview.getString("username");
        detected_callmode = from_listview.getString("mode");

        layout = (RelativeLayout)findViewById(R.id.ptt_background);

        try{
            call_mode = Integer.parseInt(detected_callmode);
        }catch(NumberFormatException nfe){
            Log.i("test", "call mode test");
        }

        tv_callname.setText(detected_username);
        tv_callmode.setText(detected_callmode);

        Button pttButton = (Button) findViewById(R.id.pttButton);

        pttButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                int action = motionEvent.getAction();
                if (action == MotionEvent. ACTION_DOWN ) {
                    layout.setBackgroundResource(R.drawable.ptt_button2);
                    Toast toast = Toast.makeText(getApplicationContext(), "Transmitting...", Toast.LENGTH_SHORT);
                    toast.show();
                    ZelloWrapper.pttStart();
                } else if (action == MotionEvent. ACTION_UP || action == MotionEvent. ACTION_CANCEL ) {
                    layout.setBackgroundResource(R.drawable.ptt_button1);
                    ZelloWrapper.pttStop();
                    Toast toast = Toast.makeText(getApplicationContext(), "Voice message sent.", Toast.LENGTH_SHORT);
                    toast.show();
                }
                return false;
            }
        });
    }
}
