package com.example.dqw648.moto;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.example.dqw648.moto.R.id.tv_num_target;
import static com.example.dqw648.moto.R.id.tv_result;

public class MainActivity extends AppCompatActivity {

    //String

    //Button
    private ImageButton btn_camera;
    private ImageView img_receive_snapshot;

    //TextView

    //ListView
    private ListView lv_identity;
    private ArrayList<String> data_list;

    //DataBase
    DatabaseHelper myDB;
    ArrayList<User> userList;
    ListView listView;
    User user;

    //variables
    private int count = 0;
    public String cur_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Button
        btn_camera = (ImageButton) findViewById(R.id.btn_camera);

        //ImageView
        img_receive_snapshot = (ImageView) findViewById(R.id.img_receive_snapshot); //Will used to set the snapshot image

        //ListView
        lv_identity = (ListView) findViewById(R.id.lv_identity);
        data_list = new ArrayList<String>();

        //Buttons
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temp_return_string("yang");
            }
        });
    }

    public void temp_return_string(String get_name){

        myDB = new DatabaseHelper(this);

        userList = new ArrayList<>();
        Cursor data = myDB.getListContents();
        int numRows = data.getCount();

        if(numRows == 0){
            Toast.makeText(MainActivity.this,"The Database is empty  :(.",Toast.LENGTH_LONG).show();
        }else{
            while(data.moveToNext()){
                user = new User(data.getString(1),data.getString(2),data.getString(3));
                if(get_name.equals(data.getString(1))){
                    data_list.add("");
                    cur_name = data.getString(1);
                    lv_identity.setAdapter(new MyListAdapter(this, R.layout.result_after_snapshot_list, data_list));
                }
            }

        }

    }

    private class MyListAdapter extends ArrayAdapter{
        private int layout;
        public MyListAdapter(Context context,int resource,List objects) {
            super(context, resource, objects);
            layout=resource;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder mainViewHolder = null;
            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout,parent,false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.tv_result = (TextView)convertView.findViewById(tv_result);
                viewHolder.tv_num_target = (TextView)convertView.findViewById(tv_num_target);
                viewHolder.btn_view_result = (Button) convertView.findViewById(R.id.btn_view_result);

                viewHolder.tv_result.setText(cur_name);
                viewHolder.tv_num_target.setText(position + "");

                viewHolder.btn_view_result.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(),"This is position:" + position, Toast.LENGTH_SHORT).show();

                        Intent ptt_interface = new Intent(MainActivity.this,PTT_Call.class);
                        ptt_interface.putExtra("username",cur_name);
                        startActivity(ptt_interface);
                    }
                });
                convertView.setTag(viewHolder);
            }else{
                mainViewHolder = (ViewHolder) convertView.getTag();
                mainViewHolder.tv_num_target.setText((Integer) getItem(position));
            }


            return convertView;
        }

        public class ViewHolder{
            TextView tv_num_target, tv_result;
            Button btn_view_result;
        }
    }

    public void DataBase(View view){
                Intent camera_view = new Intent(MainActivity.this, FirstResponderRegisteration.class);
                startActivity(camera_view);
    }
}
