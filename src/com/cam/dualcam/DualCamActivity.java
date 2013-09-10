package com.cam.dualcam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.cam.dualcam.utility.*;
import com.cam.dualcam.bitmap.*;
import com.cam.dualcam.view.CameraPreview;

@SuppressLint("NewApi")
public class DualCamActivity extends Activity implements OnClickListener {
	
	//TAG
	public static String TAG = "DualCamActivity";

	public static final int SHOWTIME = 3000;
	public static final int CAMERA_REQUEST = 100;
	public static final int REQUEST_CODE = 1;
	public Bitmap bitmap;
 	public ImageView cumShotPreviewTop,cumShotPreviewBottom;
 	public Button button;
 	
 	//Utility
 	public ImageView smileyButton, saveButton, retryButton, shareButton;
 	
 	public Uri photoUri;
	public Integer resultSet = 0;
	
	public Intent sharingIntent;
	public PackageCheck packageCheck;
	public MediaUtility mediaUtility;
	public CameraUtility cameraUtility;
	public BitmapResizer bitmapResizer;
	
	public Parameters param;
	public Camera mCamera;
    public CameraPreview mPreview;
	public FrameLayout preview, previewBack, previewFront;
	public RelativeLayout previewLayout;
	public LinearLayout pictureLayout,topL,bottomL;
	public ViewGroup prevGroup;
	
	public String phoneModel = android.os.Build.MODEL;
	public String side, fileName;
	
	public Integer shortHeight;
	public Integer shortWidth;
	public Integer screenHeight;
	public Integer screenWidth;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dualcam);
		
		screenHeight = new PhoneChecker(this).screenHeight;
		screenWidth = new PhoneChecker(this).screenWidth;
		shortHeight = (int)(screenHeight * 0.4);
		shortWidth = (int)(screenWidth * 0.4);
		
		bitmapResizer= new BitmapResizer(getApplicationContext());
		mediaUtility = new MediaUtility(getApplicationContext());
		packageCheck = new PackageCheck(getApplicationContext());
		//imageView = (ImageView) findViewById(R.id.cumshot);
		//button = (Button) findViewById(R.id.cumbutton);
		//button.setOnClickListener(this);//takePicture(ShutterCallback, PictureCallback, PictureCallback, PictureCallback)
		
		smileyButton = (ImageView) findViewById(R.id.smileyButton);
		saveButton   = (ImageView) findViewById(R.id.saveButton);
		retryButton  = (ImageView) findViewById(R.id.retryButton);
		shareButton  = (ImageView) findViewById(R.id.shareButton);
		cumShotPreviewTop = (ImageView) findViewById(R.id.cumPreviewBack);
		cumShotPreviewBottom = (ImageView) findViewById(R.id.cumPreviewFront);
		
		prevGroup = (ViewGroup) findViewById(R.id.addCamPreview);
		pictureLayout = (LinearLayout) findViewById(R.id.picLayout);
		topL = (LinearLayout) findViewById(R.id.top);
		bottomL = (LinearLayout) findViewById(R.id.bottom);
		//previewLayout = (RelativeLayout) findViewById(R.id.addCamPreview);
		//pictureLayout= (RelativeLayout) findViewById(R.id.picPreview);
		
		//cumShotPreviewTop.setOnClickListener(this);
		//cumShotPreviewBottom.setOnClickListener(this);
		smileyButton.setOnClickListener(this);
		saveButton.setOnClickListener(this);
		retryButton.setOnClickListener(this);
		shareButton.setOnClickListener(this);
		cumShotPreviewBottom.setOnClickListener(this);
		//Defaul, back camera is initiated
		side = "BACK";
		//releaseCamera();
		//previewBack.removeAllViews();
		seePreview(side);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dual_cam, menu);
		return true;
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
	}
	
	public void onClick(View view) {
		try{
			if(view.getId() == R.id.smileyButton){
				
				if(mCamera != null){
					//Toast.makeText(getApplicationContext(),"Before Nice shot!",SHOWTIME).show();
					//mCamera.takePicture(null, null, mPictureS3FIX);
					//s3Fix();
					try{
						//mCamera.takePicture(null, null, s3FixIloveS3);
						mCamera.setErrorCallback(ec);
						mCamera.takePicture(null, null, mPicture);
						//Toast.makeText(getApplicationContext(),"Nice shot!",SHOWTIME).show();
						
					}catch(Exception e){
						//mCamera.takePicture(null, null, s3FixIloveS3);
						Toast.makeText(getApplicationContext(),"Nice phone! I love S3!!!",SHOWTIME).show();
						
					}
					
				}
				else
					Toast.makeText(getApplicationContext(),"Choose a preview first.",SHOWTIME).show();
			}
			
			else if(view.getId() == R.id.saveButton){
				
				if(cumShotPreviewBottom.getDrawable() != null && cumShotPreviewBottom.getBackground() == null)
					try{
						pictureLayout.buildDrawingCache();
						saveImage(pictureLayout.getDrawingCache());
						pictureLayout.destroyDrawingCache();
					}catch(Exception e){
						Toast.makeText(getApplicationContext(),"Something happened, I'm so sorry :(",SHOWTIME).show();
					}
				else
					Toast.makeText(getApplicationContext(),"You don't want a pic of yourself?",SHOWTIME).show();
				
			}
			
			else if(view.getId() == R.id.retryButton){
				linkSTART();
			}
			
			else if(view.getId() == R.id.shareButton){
				if(fileName != null)
					shareFunction();
				else
					Toast.makeText(getApplicationContext(),"You might want to save the photo first :D",SHOWTIME).show();
				
			}
			
			else if(view.getId() == R.id.cumPreviewFront){
				if(side == "BACK")
				{
					try{
						//mCamera.takePicture(null, null, s3FixIloveS3);
						mCamera.setErrorCallback(ec);
						mCamera.takePicture(null, null, mPicture);
						//Toast.makeText(getApplicationContext(),"Nice shot!",SHOWTIME).show();
						
					}catch(Exception e){
						//mCamera.takePicture(null, null, s3FixIloveS3);
						Toast.makeText(getApplicationContext(),"Nice phone! I love S3!!!",SHOWTIME).show();
						
					}
				}
			}
		}
		catch(Exception e)
		{
			Log.i(TAG,"Error in here View = "+view.getId()+": Cause? I don't effing know -> "+e.getMessage());
			Toast.makeText(this,"Sorry, something went wrong with the camera. Error : "+e.getCause(),SHOWTIME).show();
		}
	}
	
	public ErrorCallback ec = new ErrorCallback(){

		@Override
		public void onError(int data, Camera camera) {
			Log.i(TAG,"ErrorCallback received");
			Toast.makeText(getApplicationContext(),"Sorry, something went wrong with the camera. Error",SHOWTIME).show();
		}
		
	};
	
	public PictureCallback mPicture = new PictureCallback() {

	    @Override
	    public void onPictureTaken(byte[] data, Camera camera) {
	    	
	    	//Toast.makeText(getApplicationContext(),"1",1000).show();
	    	BitmapFactory.Options options = new BitmapFactory.Options();
 	  		options.inSampleSize = 1;
 	  		options.inJustDecodeBounds = true;
 	  		// Calculate inSampleSize
 		    options.inSampleSize = bitmapResizer.calculateInSampleSize(options, shortWidth, shortHeight);
 		    
 		    // Decode bitmap with inSampleSize set
 		    options.inJustDecodeBounds = false;
	    	ImageView buttonView = getPressedPreview(side);
	    	int width = 0;
	    	int height = 0;
	    	mCamera.stopPreview();
	    	Log.i(TAG,"Pic taken");
	        try {
	        	// Image captured and saved to fileUri specified in the Intent
				// We need to recyle unused bitmaps
	            if (bitmap != null) {
	              bitmap.recycle();
	            }

		    	//Toast.makeText(getApplicationContext(),"2",1000).show();
	            if(side == "BACK")
	            {
	            	Matrix matrix = new Matrix(); 
	 	            matrix.postRotate(90); 
	 	            
	 	            //bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
	 	            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
	 	            
	 	            width = balancePhoto(screenWidth,bitmap.getWidth());
	 	            height= balancePhoto(screenHeight,bitmap.getHeight());
	 		    	//Toast.makeText(getApplicationContext(),"3",1000).show();
	 	            Log.i(TAG,width+":"+height+"   =   "+bitmap.getWidth()+":"+bitmap.getHeight());
	 	            Bitmap bmp=Bitmap.createBitmap(bitmap, 0,0,Math.round(width/2), height, matrix, true);
	 	            //Bitmap bmp=Bitmap.createBitmap(bitmap, 0,0,Math.round(bitmap.getWidth() /2)+1, bitmap.getHeight());
	 	            buttonView.setImageBitmap(bmp);
	 	            bitmap = null;
		            bmp = null;
	 	            
	 	            //imageView.setImageBitmap(bitmap);
	 	            Log.i(TAG,"To be saved2");
	            	side = "FRONT";
	            	releaseCamera();
	            	seePreview(side);
	            	
	            }
	            else
	            {
	            	Matrix matrix = new Matrix(); 
	 	            matrix.postRotate(270); 
	 	            //bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
	 	            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
	 	            width = balancePhoto(topL.getWidth(),bitmap.getWidth());
	 	            height= balancePhoto(topL.getHeight(),bitmap.getHeight());
	 	            Log.i(TAG,width+":"+height+"   =   "+bitmap.getWidth()+":"+bitmap.getHeight());
	 	            //Log.i(TAG,topL.getWidth()+":"+ topL.getHeight()+"   =   "+bitmap.getWidth()+":"+bitmap.getHeight());
	 	            Bitmap bmp=Bitmap.createBitmap(bitmap, 0,0,Math.round(width/2), height, matrix, true);
	 	            //Bitmap bmp=Bitmap.createBitmap(bitmap, 0,0,Math.round(bitmap.getWidth() /2)+1, bitmap.getHeight());
	 	            buttonView.setImageBitmap(bmp);
	 	            bitmap = null;
		            bmp = null;
	            	
	            }
	            //FileOutputStream fos = new FileOutputStream(pictureFile);
	            //fos.write(data);
	            //fos.close();
	        }catch (Exception e) {
	        	Log.i(TAG,"not saved");
	        	Log.e(TAG,"Error accessing file: " + e.getMessage());
	        	Toast.makeText(getApplicationContext(),"Japan phone KAWAII!! :D error = "+e.getCause(),SHOWTIME).show();
	        	//linkSTART();
	        	
	        }
	        
	    }
	    
	};
	
	public int balancePhoto(int viewSize, int bitmapSize){
		if(viewSize > bitmapSize)
			return bitmapSize;
		else		
			return viewSize;
	}

	public void saveImage(Bitmap bmp){
		try {

	           //File pictureFile = mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE);
			   //String fileName = 
			   Log.d(TAG,"the filename = "+mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE).toString());
			   fileName = mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE).toString();
			   Log.d(TAG,"The utility = "+mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE).toString());
		       FileOutputStream out = new FileOutputStream(mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE));
		       Log.d(TAG,"Before saving");
		       bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
		       Log.d(TAG,"After saving");
		       mediaUtility.updateMedia(TAG,"file://" +mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE).toString());
		       Log.d(TAG,"file://" +mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE).toString());
		       out.flush();
		       out.close();
		       Log.d(TAG,"Saved to "+mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE).toString());
		       Toast.makeText(getApplicationContext(),"Save successfull :D"+mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE),SHOWTIME).show();
		} catch (Exception e) {
		       e.printStackTrace();
		       Log.d(TAG,"Saving failed cause = "+ e.getCause());
		       
		       Toast.makeText(getApplicationContext(),"Some thing went wrong in saving.",SHOWTIME).show();
				
		}
	}
	
	public ImageView getPressedPreview(String side){
		ImageView buttonView = null;
		
		if(side == "BACK")
			buttonView = cumShotPreviewTop;
        if(side == "FRONT")
        	buttonView = cumShotPreviewBottom;
        
		return buttonView;
	}
	
	
	public void seePreview(String side){
		try{
			
		releaseCamera();
		ImageView buttonView = getPressedPreview(side);
		cameraUtility = new CameraUtility(getApplicationContext());
		mCamera = cameraUtility.getCameraInstance(side);
		setOrientation();
		mPreview = new CameraPreview(getApplicationContext(), mCamera);
		preview = (FrameLayout) findViewById(R.id.cumshot);
		preview.removeAllViews();
		preview.addView(mPreview);


			buttonView.setBackgroundDrawable(null);
			//buttonView.setBackground(null);
			buttonView.setImageBitmap(null);
			
		}catch(Exception e){
			Log.e(TAG,"Di ko na alam to wtf ftw");
			//Toast.makeText(getApplicationContext(),"OOPS!! Error = "+e.getMessage(),SHOWTIME).show();
			Toast.makeText(getApplicationContext(),"Japan S3 KAWAII!! :D error = "+e.getCause(),SHOWTIME).show();
        	//linkSTART();
		}
	}
	
	public void setOrientation(){
		 CameraInfo info = new CameraInfo();
		 int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
	     int degrees = 0;
	     switch (rotation) {
	         case Surface.ROTATION_0: degrees = 0; break;
	         case Surface.ROTATION_90: degrees = 90; break;
	         case Surface.ROTATION_180: degrees = 180; break;
	         case Surface.ROTATION_270: degrees = 270; break;
	     }
	     
	     int result;
	     Log.d(TAG,"Degrees = "+degrees);
	     if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	         result = (info.orientation + degrees + 90) % 360;
	         result = (360 - result) % 360;  // compensate the mirror
	     } else {  // back-facing
	         result = (info.orientation - degrees + 360 + 90) % 360;
	     }
	     boolean a = mCamera==null;
	     Log.d(TAG,"mCamera = "+a);
	     
		mCamera.setDisplayOrientation(result);
	}
	/*
	private Camera.Size getBestPreviewSize(List<Camera.Size> previewSizes, int width, int height) {
        double targetAspect = (double)width / (double)height;

		ArrayList<Camera.Size> matchedPreviewSizes = new ArrayList<Camera.Size>();
		final double ASPECT_TOLERANCE = 0.1;
		for(Size previewSize : previewSizes) {
		        double previewAspect = (double)previewSize.width / (double)previewSize.height;
		
		        // Original broken code.
		        //if(Math.abs(targetAspect - previewAspect) < ASPECT_TOLERANCE) {
		        //        matchedPreviewSizes.add(previewSize);
		        //}
		
		        // Working code.
		        if(Math.abs(targetAspect - previewAspect) < ASPECT_TOLERANCE &&
		                    previewSize.width <= width && previewSize.height <= height) {
		                matchedPreviewSizes.add(previewSize);
		        }
		}
		
		Camera.Size bestPreviewSize;
		if(!matchedPreviewSizes.isEmpty()) {
		        bestPreviewSize = Collections.max(matchedPreviewSizes, sizeComparator);
		} else {
		        bestPreviewSize = Collections.max(previewSizes, sizeComparator);
		}
		
		return bestPreviewSize;
		}
	*/
	@Override
    protected void onPause() {
        super.onPause();
        releaseCamera();              // release the camera immediately on pause event
        finish();
    }
	
	 
	public void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }
	
	public void linkSTART(){
		finish();
		Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage( getBaseContext().getPackageName() );
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}
	
	public void shareFunction(){
		//"android.resource://" + getPackageName() + "/" +
		Uri uri = Uri.parse("file://"+fileName);

		//Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" +R.drawable.icon);
		String shareBody = "Here is the share content body";
		sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("image/png");
		//sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
		//sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
		sharingIntent.putExtra(Intent.EXTRA_STREAM,uri);
		startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}

}
