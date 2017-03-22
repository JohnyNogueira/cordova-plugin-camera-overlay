package com.example.sample.plugin;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;

import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PluginName extends CordovaPlugin {

  private CallbackContext callbackContext;
      protected final static String[] permissionsG = { Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE };

  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
      this.callbackContext = callbackContext;
        Context context = cordova.getActivity().getApplicationContext();
        if(action.equals("new_activity")) {
        //    this.openNewActivity(context);
//          PluginResult r = new PluginResult(PluginResult.Status.NO_RESULT);
//          r.setKeepCallback(true);
//          callbackContext.sendPluginResult(r);
          if(!PermissionHelper.hasPermission(this, permissionsG[0])) {
            PermissionHelper.requestPermission(this, 0, Manifest.permission.CAMERA);
          } else {
            Intent i = new Intent(context, NewActivity.class);
            //   i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.cordova.startActivityForResult(this,i, 0);
            return true;
          }

        }
        return false;
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                          int[] grantResults) throws JSONException {
      Context context = cordova.getActivity().getApplicationContext();
      for (int r : grantResults) {
        if (r == PackageManager.PERMISSION_DENIED) {
          this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, 2));
          return;
        }
      }
      switch (requestCode) {
        case 0:
          if(!PermissionHelper.hasPermission(this, permissionsG[1])) {
            PermissionHelper.requestPermission(this, 3, Manifest.permission.READ_EXTERNAL_STORAGE);
          } else {
            Intent i = new Intent(context, NewActivity.class);
            //   i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.cordova.startActivityForResult(this,i, 0);

          }


          break;
        case 3:
          Intent i = new Intent(context, NewActivity.class);
          //   i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          this.cordova.startActivityForResult(this,i, 0);
          break;
      }
    }

    public void failPicture(String err) {
      this.callbackContext.error(err);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){



      super.onActivityResult(requestCode, resultCode, intent);

      if (requestCode == 0 && intent != null){

        String msg = intent.getStringExtra("returnedData");
        PluginResult resultado = new PluginResult(PluginResult.Status.OK, msg);
        resultado.setKeepCallback(true);
        callbackContext.sendPluginResult(resultado);

      }
//      if(resultCode == cordova.getActivity().RESULT_OK){
//        Bundle extras = intent.getExtras();// Get data sent by the Intent
//        String information = extras.getString("data"); // data parameter will be send from the other activity.
//        //tolog(information); // Shows a toast with the sent information in the other class
//        PluginResult resultado = new PluginResult(PluginResult.Status.OK, "this value will be sent to cordova");
//        resultado.setKeepCallback(true);
//        callbackContext.sendPluginResult(resultado);
//        return;
//      }else if(resultCode == cordova.getActivity().RESULT_CANCELED){
//        PluginResult resultado = new PluginResult(PluginResult.Status.OK, "canceled action, process this in javascript");
//        resultado.setKeepCallback(true);
//        callbackContext.sendPluginResult(resultado);
//        return;
//      }
//      // Handle other results if exists.
//      super.onActivityResult(requestCode, resultCode, intent);
    }

    private void openNewActivity(Context context) {
        Intent intent = new Intent(context, NewActivity.class);
        this.cordova.getActivity().startActivity(intent);
    }
}
