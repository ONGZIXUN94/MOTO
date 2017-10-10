package com.example.dqw648.moto;

/**
 * Created by DQW648 on 03-Oct-17.
 */

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;

/**
 * Created by QGKR46 on 9/24/2017.
 */

public class cameraHardware {
    public cameraHardware() {
        super();
    }

    public boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            Log.d("camerahardware","camera open");
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            Log.d("camerahardware","no able to open");
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }
}
