package com.example.dqw648.moto;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FirstResponderRegisteration extends AppCompatActivity {

    EditText et_name,et_coreid,et_identity;
    Button btnAdd,btnView;
    DatabaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_responder_registeration);

        et_name = (EditText) findViewById(R.id.et_name);
        et_coreid = (EditText) findViewById(R.id.et_coreid);
        et_identity = (EditText) findViewById(R.id.et_identity);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnView = (Button) findViewById(R.id.btnView);
        myDB = new DatabaseHelper(this);

        Cursor init_data = myDB.getListContents();
        int row = init_data.getCount();

        if(row == 0){
            for(int i=0; i<4; i++){
                switch (i){
                    case 0:
                        AddData("Min Kee","abc123", "fireman");
                        break;
                    case 1:
                        AddData("Shu Yang","abc234", "police");
                        break;
                    case 2:
                        AddData("Seng Guan", "abc345", "police");
                        break;
                    case 3:
                        AddData("Zi Xun", "abc456", "fireman");
                        break;
                    default:
                        break;

                }
            }
        }

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstResponderRegisteration.this,ViewList.class);
                startActivity(intent);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_name.getText().toString();
                String coreid = et_coreid.getText().toString();
                String identity = et_identity.getText().toString();
                if(name.length() != 0 && coreid.length() != 0 && identity.length() != 0){
                    AddData(name,coreid, identity);
                    et_name.setText("");
                    et_coreid.setText("");
                    et_identity.setText("");
                }else{
                    Toast.makeText(FirstResponderRegisteration.this,"You must put something in the text field!",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void AddData(String put_name,String put_coreid, String put_identity ){
        boolean insertData = myDB.addData(put_name,put_coreid,put_identity);

        if(insertData==true){
            Toast.makeText(FirstResponderRegisteration.this,"Successfully Entered Data!",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(FirstResponderRegisteration.this,"Something went wrong :(.",Toast.LENGTH_LONG).show();
        }
    }

    public void CLEANALL(View view){
    }
}
