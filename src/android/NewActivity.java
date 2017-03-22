package com.example.sample.plugin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class NewActivity extends Activity {
  private Camera mCamera;
  private CameraPreview mPreview;
  private RelativeLayout overlay;
  public int IMAGE_SIZE = 1024;
  Button btnClickEvent;
  public static final int MEDIA_TYPE_IMAGE = 1;
  public static final int MEDIA_TYPE_VIDEO = 2;
  private Intent intentNew = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String package_name = getApplication().getPackageName();
      // Optional: Hide the status bar at the top of the window

      mCamera = getCameraInstance();
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
      // Set the content view and get references to our views
      setContentView(getApplication().getResources().getIdentifier("activity_new", "layout", package_name));
      mPreview = new CameraPreview(this, mCamera);
      mCamera.setDisplayOrientation(90);
      FrameLayout preview = (FrameLayout) findViewById(getApplication().getResources().getIdentifier("camera_preview", "id", package_name));

      overlay = (RelativeLayout) findViewById(getApplication().getResources().getIdentifier("overlay", "id", package_name));
      preview.addView(mPreview);

      View backgroundTake = findViewById(getApplication().getResources().getIdentifier("btn_take", "id", package_name));
      Drawable bgTake = backgroundTake.getBackground();
      bgTake.setAlpha(0);

      View backGroundClose = findViewById(getApplication().getResources().getIdentifier("btn_close", "id", package_name));
      Drawable bgClose = backGroundClose.getBackground();
      bgClose.setAlpha(0);


      Button btnClose = (Button) findViewById(getApplication().getResources().getIdentifier("btn_close", "id", package_name));
      btnClose.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          // handle click
      //    mPreview.Destroy();
          returnResult(Activity.RESULT_OK, "Usercelled");
        }
      });

      Button btnTake = (Button) findViewById(getApplication().getResources().getIdentifier("btn_take", "id", package_name));
      btnTake.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          // handle click

          mCamera.takePicture(null, null, mPicture);



        }
      });
    }

    private static Uri getOutputMediaFileUri(int type){
      return Uri.fromFile(getOutputMediaFile(type));
    }


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
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
          "IMG_"+ timeStamp + ".jpg");
      } else if(type == MEDIA_TYPE_VIDEO) {
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
          "VID_"+ timeStamp + ".mp4");
      } else {
        return null;
      }

      return mediaFile;
    }

    private Uri processImage(byte[] data) throws IOException {
      // Determine the width/height of the image
      int width = IMAGE_SIZE;
      int height = IMAGE_SIZE;

      Camera.Parameters camParams = mCamera.getParameters();

// Find a preview size that is at least the size of our IMAGE_SIZE
      Camera.Size previewSize = camParams.getSupportedPreviewSizes().get(0);
      for (Camera.Size size : camParams.getSupportedPreviewSizes()) {
        if (size.width >= IMAGE_SIZE && size.height >= IMAGE_SIZE) {
          previewSize = size;
          break;
        }
      }
      camParams.setPreviewSize(previewSize.width, previewSize.height);

// Try to find the closest picture size to match the preview size.
      Camera.Size pictureSize = camParams.getSupportedPictureSizes().get(0);
      for (Camera.Size size : camParams.getSupportedPictureSizes()) {
        if (size.width == previewSize.width && size.height == previewSize.height) {
          pictureSize = size;
          break;
        }
      }
      camParams.setPictureSize(pictureSize.width, pictureSize.height);

      // Load the bitmap from the byte array
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inPreferredConfig = Bitmap.Config.ARGB_8888;
      Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

      // Rotate and crop the image into a square
      int croppedWidth = (width > height) ? height : width;
      int croppedHeight = (width > height) ? height : width;
      Matrix matrix = new Matrix();
      matrix.postRotate(90);
      Bitmap cropped = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getHeight(), bitmap.getHeight(), matrix, false);
      bitmap.recycle();

      // Scale down to the output size
      Bitmap scaledBitmap = Bitmap.createScaledBitmap(cropped, 500, 500, true);


      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
      String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), scaledBitmap, "Title", null);
      cropped.recycle();
      return Uri.parse(path);

     // return scaledBitmap;
    }


    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
      switch (requestCode) {
        case 1: {
          // If request is cancelled, the result arrays are empty.
          if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("yes","yes");

          } else {
            Log.d("yes","no");
            // permission denied, boo! Disable the
            // functionality that depends on this permission.
          }
          return;
        }
        // other 'case' lines to check for other
        // permissions this app might request
      }
    }

    public void returnResult(int code, String result) {
      mPreview.Destroy();
      intentNew = this.getIntent();
      intentNew.putExtra("returnedData", result);
      setResult(code, intentNew);
      if (Build.VERSION.SDK_INT >= 21) { //Is the user running Lollipop or above?
        finishAndRemoveTask(); //If yes, run the new fancy function to end the app and remove it from the Task Manager.
      } else {
        finish(); //If not, then just end the app (without removing the task completely).
      }
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

      @Override
      public void onPictureTaken(byte[] data, Camera camera) {
        try {
          Uri btm = processImage(data);

          returnResult(Activity.RESULT_OK, btm.toString());
        } catch (IOException e) {
          e.printStackTrace();
        }
//        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
//        if (pictureFile == null){
//
//          return;
//        }
//
//        try {
//          FileOutputStream fos = new FileOutputStream(pictureFile);
//          fos.write(data);
//          fos.close();
//        } catch (FileNotFoundException e) {
//        } catch (IOException e) {
//        }
      }
    };


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
      super.onWindowFocusChanged(hasFocus);

      // Get the preview size
      int previewWidth = mPreview.getMeasuredWidth(),
        previewHeight = mPreview.getMeasuredHeight();

      // Set the height of the overlay so that it makes the preview a square
      RelativeLayout.LayoutParams overlayParams = (RelativeLayout.LayoutParams) overlay.getLayoutParams();
      overlayParams.height = previewHeight - previewWidth;
      overlay.setLayoutParams(overlayParams);
    }

    private boolean checkCameraHardware(Context context) {
      if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
        // this device has a camera
        return true;
      } else {
        // no camera on this device
        return false;
      }
    }



    public static Camera getCameraInstance(){
      Camera c = null;
      try {
        c = Camera.open(); // attempt to get a Camera instance
      }
      catch (Exception e){
        // Camera is not available (in use or does not exist)
      }
      return c; // returns null if camera is unavailable
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {


      // Do something with the result of the Intent data

      // Send parameters to retrieve in cordova.
      Intent intent = new Intent();
      intent.putExtra("data", "This is the sent information from the 2 activity :) ");
      setResult(Activity.RESULT_OK, intent);
      finish();// Exit of this activity !
    }
}
