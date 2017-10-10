package com.example.dqw648.moto;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

//import org.apache.http.HttpResponse;

/**
 * Created by QGKR46 on 9/18/2017.
 */

public class httpServiceRequest extends Service {
    private static final String TAG = "httpService";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind httpService ");
        return mBinder;
    }

    IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public httpServiceRequest getServerInstance() {
            Log.i(TAG, "getServerInstance() ");
            return httpServiceRequest.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate httpService ");
    }

    public HttpResponse<String> sendHttpRequest(String taskId, String imagePath, String token ){
        /*http post ::
        "http://cl-api.vize.ai/{your task ID}?image={path/myimage.png}"
        .header("Authorization", "JWT {your JWT token}")
        .header("Content-Type", "application/x-www-form-urlencoded")
        .header("Accept", "text/plain")
        .asString()
        */
        HttpResponse<String> request = null;
        String url = "http://cl-api.vize.ai/"+taskId+"?image="+imagePath;
        try {
            //request = Unirest.get("https://webknox-question-answering.p.mashape.com/questions/answers?question=" + msg[0] + "&answerLookup=true&answerSearch=true")
            //        .header("X-Mashape-Authorization", "<Insert your Mashape key here>")
            //        .asJson();
            request = Unirest.post(url)
                    .header("Authorization", token)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "text/plain")
                    .asString();
            Log.i(TAG,"request = "+request);
        } catch (UnirestException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return request;

        //construct http post
//        String url = "http://cl-api.vize.ai/"+taskId+"?image={"+imagePath+"}";
//        Log.i(TAG, "url= "+url);
//        HttpClient httpClient = new DefaultHttpClient();
//        HttpPost httpPost = new HttpPost(url);
//
//        httpPost.setHeader("Authorization", token);
//        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
//        httpPost.setHeader("Accept", "text/plain");
//
//        //execute httpPost
//        // Execute POST
//        Log.i(TAG,"execute httpPost ");
//        HttpResponse httpResponse = null;
//        try {
//            httpResponse = httpClient.execute(httpPost);
//            Log.i(TAG,"response= "+httpResponse);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        HttpEntity responseEntity = httpResponse.getEntity();
//        if (responseEntity != null) {
//            try {
//                response = EntityUtils.toString(responseEntity);
//                Log.i(TAG,"response= "+response);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            response = "{\"NO DATA:\"NO DATA\"}";
//        }
//        return response;

    }


}
