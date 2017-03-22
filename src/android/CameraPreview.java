package com.example.sample.plugin;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import static com.example.sample.plugin.NewActivity.getCameraInstance;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
  private SurfaceHolder mHolder;
  private Camera mCamera;

  public CameraPreview(Context context, Camera camera) {
    super(context);
    mCamera = camera;

    // Install a SurfaceHolder.Callback so we get notified when the
    // underlying surface is created and destroyed.
    mHolder = getHolder();
    mHolder.addCallback(this);
    // deprecated setting, but required on Android versions prior to 3.0
    mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
  }

  public void Destroy(){
    mCamera.stopPreview();
    mCamera.setPreviewCallback(null);
    mCamera.release();
    mCamera = null;
  }

  public void setSize(){
    Camera.Parameters camParams = mCamera.getParameters();

// Find a preview size that is at least the size of our IMAGE_SIZE
    Camera.Size previewSize = camParams.getSupportedPreviewSizes().get(0);
    for (Camera.Size size : camParams.getSupportedPreviewSizes()) {
      if (size.width >= 1024 && size.height >= 1024) {
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
  }

  public void surfaceCreated(SurfaceHolder holder) {
    // The Surface has been created, now tell the camera where to draw the preview.
    try {
      if (this.mCamera == null) {
        this.mCamera = getCameraInstance();

      } else {
        mCamera.setPreviewDisplay(holder);
        mCamera.startPreview();
      }

      setSize();
    } catch (IOException e) {

    }
  }

  public void surfaceDestroyed(SurfaceHolder holder) {
    // empty. Take care of releasing the Camera preview in your activity.
  }

  public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    // If your preview can change or rotate, take care of those events here.
    // Make sure to stop the preview before resizing or reformatting it.

    if (mHolder.getSurface() == null){
      // preview surface does not exist
      return;
    }

    // stop preview before making changes
    try {
      mCamera.stopPreview();
    } catch (Exception e){
      // ignore: tried to stop a non-existent preview
    }

    // set preview size and make any resize, rotate or
    // reformatting changes here

    // start preview with new settings
    try {
      mCamera.setPreviewDisplay(mHolder);
      mCamera.startPreview();

      setSize();

    } catch (Exception e){

    }
  }
}
