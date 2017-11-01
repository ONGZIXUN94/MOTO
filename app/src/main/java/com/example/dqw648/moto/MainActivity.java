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

import org.json.JSONException;
import org.json.JSONObject;

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

    //String
    String user_acc_name = "shu yang";
    String user_team;

    //Button
    private ImageButton btn_camera;
    private ImageView img_receive_snapshot;

    //TextView

    //ListView
    private ListView lv_identity;

    //DataBase
    DatabaseHelper myDB = new DatabaseHelper(this);
    ArrayList<User> userList = new ArrayList<>();
    ArrayList<User> data_list;
    User user;


    //Image upload to server
    boolean check = true;
    String ImageName = "image_name" ;
    String ImagePath = "image_path" ;
    //String ServerUploadPath ="https://androidlibrary.000webhostapp.com/LibraryApp/development/img_upload_to_server.php" ;
    //String ServerUploadPath2 ="https://androidlibrary.000webhostapp.com/LibraryApp/development/img_upload_to_server_police.php";
    String ServerUploadPath ="http://150.130.67.65:10080/first_responder/img_upload_to_server.php" ;
    String ServerUploadPath2 ="http://150.130.67.65:10080/first_responder/img_upload_to_server_police.php" ;
    String finalFireman = "";
    String finalPoliceman = "";

    //variables
    private int count = 0;
//    String cur_name, cur_coreid, cur_identity, call_mode;
    String[] cur_name = new String[10];
    String[] cur_coreid = new String[10];
    String[] cur_identity = new String[10];
    String[] call_mode = new String[10];
    int getView_count = 0;
    int num_polis = 0;
    int num_fireman = 0;


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

        Cursor init_data = myDB.getListContents();

        //Initialization
        int Rows = init_data.getCount();

        if(user_acc_name.equals("seng guan") || user_acc_name.equals("shu yang")){
            user_team = "police";
        }else{
            user_team = "fireman";
        }

        if(Rows == 0){
            Toast.makeText(MainActivity.this,"The Database is empty  :(.",Toast.LENGTH_LONG).show();
        }else {
            count = 0;
            while (init_data.moveToNext()) {
                user = new User(init_data.getString(1), init_data.getString(2), init_data.getString(3));
                count++;

                if(init_data.getString(3).equals("police"))
                {
                    num_polis++;
                }else{
                    num_fireman++;
                }


                if (user_team.equals(init_data.getString(3))) {
                    cur_name[0] = init_data.getString(3);
                    cur_coreid[0] = count + "";
                    cur_identity[0] = "Group Call";
                    call_mode[0] = "2";
                }
            }
            data_list.add(user);
            lv_identity.setAdapter(new MyListAdapter(this, R.layout.result_after_snapshot_list, data_list));
        }

        //Buttons
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
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

    public void analyser_result(String get_name, String get_identity){


        Cursor data = myDB.getListContents();
        int numRows = data.getCount();

        int same_team_count = 0;
        int diff_team_count = 0;
        int analyser_count = 1;
        data_list.clear();
        lv_identity.setAdapter(new MyListAdapter(this, R.layout.result_after_snapshot_list, data_list));
        getView_count = 0;

        if(numRows == 0){
            Toast.makeText(MainActivity.this,"The Database is empty  :(.",Toast.LENGTH_LONG).show();
        }else{
            while(data.moveToNext()){

                user = new User(data.getString(1),data.getString(2),data.getString(3));
                //if same team
                if(get_identity.equals(user_team) && get_identity.equals(data.getString(3))){
                    same_team_count++;
                    cur_name[0] = data.getString(3);
                    cur_coreid[0] = same_team_count + "";
                    cur_identity[0] = "Group Call";
                    call_mode[0] = "2";
                    if(same_team_count == num_polis || same_team_count == num_fireman)
                    {
                        data_list.add(user);
                    }
                }

                //if different team
                if(!get_identity.equals(user_team)){
                    diff_team_count++;
                    analyser_count = 2;
                    cur_name[1] = "Area Team";
                    cur_coreid[1] = diff_team_count + "";
                    cur_identity[1] = "Group Call";
                    call_mode[1] = "2";
                    data_list.add(user);
                    if(diff_team_count == num_polis + num_fireman)
                    {
                        data_list.add(user);
                    }
                }

                if(get_name.equals(data.getString(1)) && get_identity.equals(data.getString(3))){
                    cur_name[analyser_count] = get_name;
                    cur_coreid[analyser_count] = 1 + "";
                    cur_identity[analyser_count] = "Private Call";
                    call_mode[analyser_count] = "1";
                    data_list.add(user);
                }
            }
            lv_identity.setAdapter(new MyListAdapter(this, R.layout.result_after_snapshot_list, data_list));
        }

    }

    private class MyListAdapter extends ArrayAdapter{
        private int layout;
        private int count_array;
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

                viewHolder.tv_result.setText("Team: " + cur_name[position]);
                viewHolder.tv_coreid.setText("Member: " + cur_coreid[position]);
                viewHolder.tv_identity.setText("Mode: " + cur_identity[position]);

                getView_count++;
                viewHolder.btn_view_result.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent ptt_interface = new Intent(MainActivity.this,PTT_Call.class);
                        ptt_interface.putExtra("mode",call_mode[position]);
                        ptt_interface.putExtra("username",cur_name[position]);
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
                    analyser_result("zi xun","fireman");
                    Log.i("test", "onActivityResult OK");

                    ImageUploadToServerFunction();
                    Log.i("test", "ImageUploadToServerFunction()");

                    Log.i("ImageUpload","call send upload");
                    ImageUploadToServerFunction2();
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
                Log.i("ImageUpload","string1="+string1);
                // Setting image as transparent after done uploading.
                // imageView.setImageResource(android.R.color.transparent);
                //bDone = true;

              //  string1 = "{  \"cached\": true,  \"classification_time\": 0.2783019542694092,  \"classifier_load_time\": 1.2159347534179688e-05,  \"prediction\": \"FiremanCap\",  \"preprocess_time\": 0.008861064910888672,  \"scores\": {    \"FiremanBadge\": 0.004154800903052092,    \"FiremanCap\": 0.8166045546531677,    \"FiremanOthers\": 0.10914599150419235,    \"FiremanUniform\": 0.07009470462799072  }}";
                Log.i("ImageUpload", "start json=" + string1);
                //string1 = " {  \"cached\": true,  \"classification_time\": 0.35744714736938477,  \"classifier_load_time\": 2.7179718017578125e-05,  \"prediction\": \"FiremanBadge\",  \"preprocess_time\": 0.011495113372802734,  \"scores\": {    \"FiremanBadge\": 0.9862809181213379,    \"FiremanCap\": 0.003187180031090975,    \"FiremanOthers\": 0.001125816022977233,    \"FiremanUniform\": 0.009406168013811111  }}";
                JSONObject jsonStr;
                try {
                    jsonStr = new JSONObject(string1);
                    String prediction = jsonStr.getString("prediction");
                    Log.i("ImageUpload", "start json=" + prediction);
                    if(prediction.equalsIgnoreCase("FiremanBadge")|| prediction.equalsIgnoreCase("FiremanCap") || prediction.equalsIgnoreCase("FiremanUniform")) {
                        finalFireman = "Fireman";
                    }
                    else{
                        finalFireman = "Others";
                    }
                    Log.i("ImageUpload", "start json=" + finalFireman);
                    Toast.makeText(MainActivity.this,finalFireman,Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

    public void ImageUploadToServerFunction2(){

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
            protected void onPostExecute(String string2) {

                super.onPostExecute(string2);

                // Dismiss the progress dialog after done uploading.
                //progressDialog.dismiss();

                // Printing uploading success message coming from server on android app.
                // Toast.makeText(UploadImage.this,string2,Toast.LENGTH_LONG).show();
                Log.i("ImageUpload", "string2=" + string2);
                // Setting image as transparent after done uploading.
                // imageView.setImageResource(android.R.color.transparent);
//                string2 = "{" +
//                        " \"cached\": false,\n" +
//                        " \"classification_time\": 0.29324793815612793,\n" +
//                        " \"classifier_load_time\": 0.5185329914093018,\n" +
//                        " \"prediction\": \"PolicemanBadge\",\n" +
//                        " \"preprocess_time\": 0.018055200576782227,\n" +
//                        " \"scores\": {\n" +
//                        "   \"PolicemanBadge\": 0.8097599744796753,\n" +
//                        "   \"PolicemanCap\": 0.1452029049396515,\n" +
//                        "   \"PolicemanOthers\": 0.03941991925239563,\n" +
//                        "   \"PolicemanUniform\": 0.005617136601358652\n" +
//                        " }\n" +
//                        "}";

                Log.i("ImageUpload", "start json=" + string2);
                JSONObject jsonStr;
                try {
                    jsonStr = new JSONObject(string2);
                    String prediction = jsonStr.getString("prediction");
                    Log.i("ImageUpload", "start json=" + prediction);
                    if (prediction.equalsIgnoreCase("PolicemanBadge") || prediction.equalsIgnoreCase("PolicemanCap") || prediction.equalsIgnoreCase("PolicemanUniform")) {
                        finalPoliceman = "Policeman";
                    } else {
                        finalPoliceman = "Others";
                    }
                    Log.i("ImageUpload", "start json=" + finalPoliceman);
                    Toast.makeText(MainActivity.this,finalPoliceman,Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();

                HashMapParams.put(ImageName, "IMG_1.jpg");


                HashMapParams.put(ImagePath, ConvertImage);

                String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath2, HashMapParams);

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
