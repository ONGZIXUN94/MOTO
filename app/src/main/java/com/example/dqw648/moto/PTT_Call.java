package com.example.dqw648.moto;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PTT_Call extends AppCompatActivity {

    //String
    String detected_username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptt__call);
        Bundle from_listview = getIntent().getExtras();
        detected_username = from_listview.getString("username");
    }

    public void ptt_call(){

    }

    public void ptt_start(){

    }

    public void ptt_stop(){

    }
}
