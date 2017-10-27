package com.example.dqw648.moto;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.util.Base64;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static com.example.dqw648.moto.CameraView.REQUEST_IMAGE_CAPTURE;
import static com.example.dqw648.moto.R.id.tv_coreid;
import static com.example.dqw648.moto.R.id.tv_identity;
import static com.example.dqw648.moto.R.id.tv_result;

public class MainActivity extends AppCompatActivity {

    public static Camera mCamera;
    private static final String TAG = "mainApp";
    private Bitmap mCameraBitmap;

    String ImageName = "image_name" ;

    String ImagePath = "image_path" ;
    //String

    //Button
    private ImageButton btn_camera;
    private ImageView img_receive_snapshot;

    ProgressDialog progressDialog ;
    boolean check = true;
    String ServerUploadPath ="https://androidlibrary.000webhostapp.com/LibraryApp/development/img_upload_to_server.php" ;
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

                   // ImageUploadToServerFunction();
                    Log.i("test", "ImageUploadToServerFunction()");

                }
            }
        }
    }


    public void ImageUploadToServerFunction(){

        ByteArrayOutputStream byteArrayOutputStreamObject ;

        byteArrayOutputStreamObject = new ByteArrayOutputStream();

        mCameraBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

               // progressDialog = ProgressDialog.show(UploadImage,"Image is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                // Dismiss the progress dialog after done uploading.
                //progressDialog.dismiss();

                // Printing uploading success message coming from server on android app.
               // Toast.makeText(UploadImage.this,string1,Toast.LENGTH_LONG).show();
                Log.i("ImageUpload",string1);
                // Setting image as transparent after done uploading.
               // imageView.setImageResource(android.R.color.transparent);


            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();

                HashMapParams.put(ImageName, "IMG_1.jpg");


                HashMapParams.put(ImagePath, ConvertImage);

                String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();

        AsyncTaskUploadClassOBJ.execute();
    }

    public class ImageProcessClass{

        public String ImageHttpRequest(String requestURL,HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {

                URL url;
                HttpURLConnection httpURLConnectionObject ;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject ;
                BufferedReader bufferedReaderObject ;
                int RC ;

                url = new URL(requestURL);

                httpURLConnectionObject = (HttpURLConnection) url.openConnection();

                httpURLConnectionObject.setReadTimeout(19000);

                httpURLConnectionObject.setConnectTimeout(19000);

                httpURLConnectionObject.setRequestMethod("POST");

                httpURLConnectionObject.setDoInput(true);

                httpURLConnectionObject.setDoOutput(true);

                OutPutStream = httpURLConnectionObject.getOutputStream();

                bufferedWriterObject = new BufferedWriter(

                        new OutputStreamWriter(OutPutStream, "UTF-8"));

                bufferedWriterObject.write(bufferedWriterDataFN(PData));

                bufferedWriterObject.flush();

                bufferedWriterObject.close();

                OutPutStream.close();

                RC = httpURLConnectionObject.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReaderObject.readLine()) != null){

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            StringBuilder stringBuilderObject;

            stringBuilderObject = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {

                if (check)

                    check = false;
                else
                    stringBuilderObject.append("&");

                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilderObject.append("=");

                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilderObject.toString();
        }

    }

}
