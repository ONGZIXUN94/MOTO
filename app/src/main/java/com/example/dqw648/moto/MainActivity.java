package com.example.dqw648.moto;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import com.zello.sdk.Zello;

import java.util.ArrayList;
import java.util.List;

import static com.example.dqw648.moto.CameraView.REQUEST_IMAGE_CAPTURE;
import static com.example.dqw648.moto.R.id.tv_coreid;
import static com.example.dqw648.moto.R.id.tv_identity;
import static com.example.dqw648.moto.R.id.tv_result;

public class MainActivity extends AppCompatActivity {

    public static Camera mCamera;
    private static final String TAG = "mainApp";
    private Bitmap mCameraBitmap;

    //String

    //Button
    private ImageButton btn_camera;
    private ImageView img_receive_snapshot;

    //TextView

    //ListView
    private ListView lv_identity;

    //DataBase
    DatabaseHelper myDB;
    ArrayList<User> userList;
    ArrayList<User> data_list;
    ListView listView;
    User user;
    Cur_User Cur_User;

    //variables
    private int count = 0;
    public String cur_name, cur_coreid, cur_identity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ZelloWrapper.init(this);
        //Button
        btn_camera = (ImageButton) findViewById(R.id.btn_camera);

        //ImageView
        img_receive_snapshot = (ImageView) findViewById(R.id.img_receive_snapshot); //Will used to set the snapshot image

        //ListView
        lv_identity = (ListView) findViewById(R.id.lv_identity);
        data_list = new ArrayList<>();

        //Buttons
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED)
                {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            1);
                }
                if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED)
                {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);
                }
                if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED)
                {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);
                }
                if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.INTERNET)
                        == PackageManager.PERMISSION_DENIED)
                {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.INTERNET},
                            1);
                }
                startActivityForResult(new Intent(MainActivity.this, CameraView.class), REQUEST_IMAGE_CAPTURE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ZelloWrapper.setStatusText(""); // Clear status message
        new ScanStatusTask().execute("");
    }

    class ScanStatusTask extends AsyncTask<String, Integer, Long> {
        protected Long doInBackground(String... strings) {
            while (ZelloWrapper.checkTrigerToJoinGroup()!=true){
            }

            return 0L;
        }

        protected void onProgressUpdate(Integer... progress) {
            // setProgressPercent(progress[0]);
        }

        protected void onPostExecute(Long result) {
            // Log.d("app", "done");
            connectToFirstResponderGroup();
        }

        private void connectToFirstResponderGroup(){
            Log.d("app", "TODO: connect to first responderGroup");
            Log.d("app", "TODO: add to list view");
            Toast.makeText(MainActivity.this, "Connect to Dynamic Group", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Zello.getInstance().unconfigure();
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
                    data_list.clear();
                    cur_name = data.getString(1);
                    cur_coreid = data.getString(2);
                    cur_identity = data.getString(3);
                    data_list.add(user);
                }
            }

            lv_identity.setAdapter(new MyListAdapter(this, R.layout.result_after_snapshot_list, data_list));
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
                viewHolder.tv_coreid = (TextView)convertView.findViewById(tv_coreid);
                viewHolder.tv_identity = (TextView)convertView.findViewById(tv_identity);
                viewHolder.btn_view_result = (Button) convertView.findViewById(R.id.btn_view_result);

                viewHolder.tv_result.setText("Name: " + cur_name);
                viewHolder.tv_coreid.setText("CoreID: " + cur_coreid);
                viewHolder.tv_identity.setText(cur_identity);

                viewHolder.btn_view_result.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent ptt_interface = new Intent(MainActivity.this,PTT_Call.class);
                        ptt_interface.putExtra("username",cur_name);
                        startActivity(ptt_interface);
                    }
                });
                convertView.setTag(viewHolder);
            }else{
                mainViewHolder = (ViewHolder) convertView.getTag();
                mainViewHolder.tv_coreid.setText((Integer) getItem(position));
            }


            return convertView;
        }

        public class ViewHolder{
            TextView tv_coreid, tv_result, tv_identity;
            Button btn_view_result;
        }
    }

    public void DataBase(View view){
                Intent camera_view = new Intent(MainActivity.this, FirstResponderRegisteration.class);
                startActivity(camera_view);
    }

    public void imgUpload(View view){
        Intent UploadImage = new Intent(MainActivity.this, UploadImage.class);
        startActivity(UploadImage);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("test", "onActivityResult");
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                if (CameraView.mCameraData != null) {
                    mCameraBitmap = BitmapFactory.decodeByteArray(CameraView.mCameraData, 0, CameraView.mCameraData.length);
                    Matrix mat = new Matrix();
                    mat.postRotate(90);
                    mCameraBitmap = Bitmap.createBitmap(mCameraBitmap, 0, 0, mCameraBitmap.getWidth(), mCameraBitmap.getHeight(), mat, true);
                    img_receive_snapshot.setImageBitmap(mCameraBitmap);
                    temp_return_string("yang");
                    Log.i("test", "onActivityResult OK");
                    ZelloWrapper.setStatusText("JoinThisF-ingGroup");
                }
            }
        }
    }



}
