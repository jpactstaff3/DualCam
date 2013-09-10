package com.cam.dualcam.utility;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.Surface;
import android.widget.Toast;

public class CameraUtility {
	//TAG
	public static String TAG = "CameraUtility";
	public int result;
	public CameraInfo info;
	public static int cameraId = -1;
	private Context context;
	private Parameters params;
	
	public CameraUtility(Context localContext){
		context = localContext;
	}

public Camera getCameraInstance(String side){
    Camera c = null;
    try {
        c = Camera.open(findCamera(side)); // attempt to get a Camera instance
        
        params = c.getParameters();
        List<Camera.Size> size = params.getSupportedPreviewSizes();
        Log.d(TAG, "Log size = "+size.size());
        Camera.Size camsize = size.get(0);
        for(int i=0;i<size.size();i++)
        {
        	if(size.get(i).width > camsize.width)
        		camsize = size.get(i);
        }
        params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
        params.setExposureCompensation(0);
        params.setPreviewSize(camsize.width, camsize.height);
        
        //params.setJpegQuality(100);
        //params.setRotation(90);
        c.setParameters(params);
        
    }
    catch (Exception e){
        // Camera is not available (in use or does not exist)
    	Log.d(TAG, "Something shit happened: e @"+e.getCause());
    	Toast.makeText(context.getApplicationContext(),"Something is wrong with the camera setting : CAUSE = "+e.getMessage(),Field.SHOWTIME).show();
    	
    }
    //Log.d(TAG, "Something shit happened: c @"+c);
    return c; // returns null if camera is unavailable
}



/** Check if this device has a camera */
public boolean checkCameraHardware() {
    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
        // this device has a camera
        return true;
    } else {
        // no camera on this device
        return false;
    }
}

public int findFrontFacingCamera() {
    int cameraId = -1;
    // Search for the front facing camera
    int numberOfCameras = Camera.getNumberOfCameras();
    for (int i = 0; i < numberOfCameras; i++) {
      info = new CameraInfo();
      Camera.getCameraInfo(i, info);
      if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
        Log.d(TAG, "Camera found");
        cameraId = i;
        break;
      }
    }
    return cameraId;
  }

public static int findCamera(String side) {
    
    // Search for the front facing camera
    int numberOfCameras = Camera.getNumberOfCameras();
    //int rotation = context.getWindowManager().getDefaultDisplay()
    //        .getRotation();
    for (int i = 0; i < numberOfCameras; i++) {
      CameraInfo info = new CameraInfo();
      Camera.getCameraInfo(i, info);
      
      if(side == "FRONT"){
    	  if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
    		  Log.d(TAG, "Front Camera found: ID @"+i);
    		  cameraId = i;
    		  
    		  break;
    	  }
      }
      /*
      else if(side == "BACK"){
    	  if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
    		  Log.d(TAG, "Back Camera found: ID @"+i);
    		  cameraId = i;
    		  break;
    	  }
      }
      */
      else {
    	  if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
    		  Log.d(TAG, "Back Camera found: ID @"+i);
    		  cameraId = i;
    		  break;
    	  }
      }
      
    }
    return cameraId;
  }


PictureCallback myPictureCallback_JPG = new PictureCallback(){

	 @Override
	 public void onPictureTaken(byte[] arg0, Camera arg1) {
	  // TODO Auto-generated method stub
	  Bitmap bitmapPicture
	   = BitmapFactory.decodeByteArray(arg0, 0, arg0.length);
	 }};


}
