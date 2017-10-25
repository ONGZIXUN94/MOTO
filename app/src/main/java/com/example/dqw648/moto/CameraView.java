package com.example.dqw648.moto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

//http://archive.oreilly.com/oreillyschool/courses/android2/CameraAdvanced.html

public class CameraView extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = "CameraView";

    //private Camera mCamera;
    private SurfaceView  mPreview;
    public static byte[] mCameraData;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final String EXTRA_CAMERA_DATA = "camera_data";
    private static final String KEY_IS_CAPTURING = "is_capturing";
    private Button captureButton, doneButton;

    private boolean mIsCapturing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);

        mPreview = (SurfaceView) findViewById(R.id.preview_view);
        final SurfaceHolder surfaceHolder = mPreview.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mIsCapturing = true;

        // Add a listener to the Capture button
        captureButton = (Button) findViewById(R.id.capture_button);
        captureButton.setOnClickListener(mCaptureImageButtonClickListener);

        doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setOnClickListener(mDoneButtonClickListener);

    }

    private View.OnClickListener mCaptureImageButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG,"take picture");
            MainActivity.mCamera.takePicture(null, null, mPicture);
        }
    };

    private View.OnClickListener mRecaptureImageButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setupImageCapture();
        }
    };

    private View.OnClickListener mDoneButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mCameraData != null) {
                Log.i(TAG,"done ok");
                setResult(RESULT_OK);
            } else {
                Log.i(TAG,"done cancel");
                setResult(RESULT_CANCELED);
            }
            Log.i(TAG,"done finish");
            finish();
        }
    };

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "onPictureTaken " );

            mCameraData = data;
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions: " );
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
              } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    //Camera Recorgnizer has been recorgnized the identity of first responer
    public void identity_recorgnized(String name, String id, String phone){

        Intent identity_result = new Intent(CameraView.this, MainActivity.class);
        identity_result.putExtra("name", name);
        identity_result.putExtra("id", id);
        identity_result.putExtra("phone", phone);
        startActivity(identity_result);
    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            // mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            //        "IMG_"+ timeStamp + ".jpg");
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +"IMG_1.jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (MainActivity.mCamera == null) {
            try {
                MainActivity.mCamera = Camera.open();
                MainActivity.mCamera.setPreviewDisplay(mPreview.getHolder());
                MainActivity.mCamera.setDisplayOrientation(90);
                if (mIsCapturing) {
                    MainActivity.mCamera.startPreview();
                }
            } catch (Exception e) {
                Log.i(TAG, "Unable to open camera.");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (MainActivity.mCamera != null) {
            releaseCamera();
            MainActivity.mCamera = null;
        }// release the camera immediately on pause event
    }


    private void releaseCamera(){
        if (MainActivity.mCamera != null){
            MainActivity.mCamera.release();        // release the camera for other applications
            MainActivity.mCamera = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putBoolean(KEY_IS_CAPTURING, mIsCapturing);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mIsCapturing = savedInstanceState.getBoolean(KEY_IS_CAPTURING, mCameraData == null);
        if (mCameraData != null) {
            setupImageDisplay();
        } else {
            setupImageCapture();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (MainActivity.mCamera != null) {
            try {
                MainActivity.mCamera.setPreviewDisplay(holder);
                if (mIsCapturing) {
                    MainActivity.mCamera.startPreview();
                }
            } catch (IOException e) {
                Log.d(TAG, "Unable to start camera preview.");
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    private void setupImageCapture() {
        mPreview.setVisibility(View.VISIBLE);
        MainActivity.mCamera.startPreview();
        captureButton.setText(R.string.capturing_image);
        captureButton.setOnClickListener(mCaptureImageButtonClickListener);
    }

    private void setupImageDisplay() {
        Bitmap bitmap = BitmapFactory.decodeByteArray(mCameraData, 0, mCameraData.length);
        //mCameraImage.setImageBitmap(bitmap);
        MainActivity.mCamera.stopPreview();
        mPreview.setVisibility(View.INVISIBLE);
        // mCameraImage.setVisibility(View.VISIBLE);
        captureButton.setText(R.string.recapture_image);
        captureButton.setOnClickListener(mRecaptureImageButtonClickListener);
    }
}

