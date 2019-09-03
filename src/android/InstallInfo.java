package com.progcap.install;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;

public class InstallInfo extends CordovaPlugin {
    private CallbackContext callbackContext;
    private InstallReferrerClient referrerClient;
    private static final String TAG = "cordova-plugin-install-info";
    private static final String ACTION_BUILD_CONNECTION = "startConnection";
    private static final String ACTION_GET_INSTALL_REFERER = "getInstallReferrer";

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        Log.d(TAG, "initialize");
        super.initialize(cordova, webView);

        // Get an instance of SmsRetrieverClient, used to start listening for a matching SMS message.
//        smsRetrieverClient = SmsRetriever.getClient(cordova.getActivity().getApplicationContext());
        referrerClient = InstallReferrerClient.newBuilder(cordova.getActivity().getApplicationContext()).build();
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        PluginResult result = null;
        this.callbackContext = callbackContext;

        if(action.equals(ACTION_BUILD_CONNECTION)) {
            this.startConnection(callbackContext);
        } else if(action.equals(ACTION_GET_INSTALL_REFERER)) {
            this.getInstallReferrer(callbackContext);
        }
        return true;
    }

    public void onDestroy() {
        if(referrerClient != null) {
            referrerClient.endConnection();
        }
    }

    private void startConnection(CallbackContext callbackContext) {

        referrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT, "No Result");
                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        result = new PluginResult(PluginResult.Status.OK, "Active");

                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        result = new PluginResult(PluginResult.Status.OK, "Not Available");

                        // API not available on the current Play Store app
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        result = new PluginResult(PluginResult.Status.OK, "Failed");

                        // Connection could not be established
                        break;
                }
                callbackContext.sendPluginResult(result);
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
    }

    private void getInstallReferrer(CallbackContext callbackContext) {
        try {
            ReferrerDetails response = referrerClient.getInstallReferrer();
            String refferName = response.getInstallReferrer();
            Long clickTime = response.getReferrerClickTimestampSeconds();
            Long beginTime = response.getInstallBeginTimestampSeconds();
            PluginResult result = new PluginResult(PluginResult.Status.OK, refferName, clickTime, beginTime);
            callbackContext.sendPluginResult(result);
        } catch(RemoteException e) {
            Log.e(TAG, "Remote Message: " + e.getMessage());
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, "Some Error");

        }
    }

    private void closeConnection(CallbackContext callbackContext) {
        referrerClient.endConnection();
    }

}



// package com.progcap.install;

// import android.os.RemoteException;
// import android.util.Log;

// import org.apache.cordova.CallbackContext;
// import org.apache.cordova.CordovaInterface;
// import org.apache.cordova.CordovaPlugin;
// import org.apache.cordova.CordovaWebView;
// import org.apache.cordova.PluginResult;
// import org.json.JSONArray;
// import org.json.JSONException;

// import com.android.installreferrer.api.InstallReferrerClient;
// import com.android.installreferrer.api.InstallReferrerStateListener;
// import com.android.installreferrer.api.ReferrerDetails;

// public class InstallInfo extends CordovaPlugin {
//     private CallbackContext callbackContext;
//     private InstallReferrerClient referrerClient;
//     private static final String TAG = "cordova-plugin-install-info";
//     private static final String ACTION_BUILD_CONNECTION = "buildConnection";
//     private static final String ACTION_GET_INSTALL_REFERER = "getInstallReferrer";

//     @Override
//     public void initialize(CordovaInterface cordova, CordovaWebView webView) {
//         Log.d(TAG, "initialize");
//         super.initialize(cordova, webView);

//         // Get an instance of SmsRetrieverClient, used to start listening for a matching SMS message.
// //        smsRetrieverClient = SmsRetriever.getClient(cordova.getActivity().getApplicationContext());
//         referrerClient = InstallReferrerClient.newBuilder(cordova.getActivity().getApplicationContext()).build();
//     }

//     public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
//         PluginResult result = null;
//         this.callbackContext = callbackContext;

//         if(action.equals(ACTION_BUILD_CONNECTION)) {
//             this.startConnection(callbackContext);
//         } else if(action.equals(ACTION_GET_INSTALL_REFERER)) {
//             this.getInstallReferrer(callbackContext);
//         }
//         return true;
//     }

//     public void onDestroy() {
//         if(referrerClient != null) {
//             referrerClient.endConnection();
//         }
//     }

//     private void startConnection(CallbackContext callbackContext) {
//         referrerClient.startConnection(new InstallReferrerStateListener() {
//             @Override
//             public void onInstallReferrerSetupFinished(int responseCode) {
//                 PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT, "No Result");
//                 switch (responseCode) {
//                     case InstallReferrerClient.InstallReferrerResponse.OK:
//                         result = new PluginResult(PluginResult.Status.OK, "Active");

//                         break;
//                     case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
//                         result = new PluginResult(PluginResult.Status.OK, "Not Available");

//                         // API not available on the current Play Store app
//                         break;
//                     case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
//                         result = new PluginResult(PluginResult.Status.OK, "Failed");

//                         // Connection could not be established
//                         break;
//                 }
//                 callbackContext.sendPluginResult(result);
//             }

//             @Override
//             public void onInstallReferrerServiceDisconnected() {
//                 // Try to restart the connection on the next request to
//                 // Google Play by calling the startConnection() method.
//             }
//         });
//     }

//     private void getInstallReferrer(CallbackContext callbackContext) {
//         try {
//             ReferrerDetails response = referrerClient.getInstallReferrer();
//             String refferName = response.getInstallReferrer();
// //            Long clickTime = response.getReferrerClickTimestampSeconds();
// //            Long beginTime = response.getInstallBeginTimestampSeconds();
//             PluginResult result = new PluginResult(PluginResult.Status.OK, refferName);
//             callbackContext.sendPluginResult(result);
//         } catch(RemoteException e) {
//             Log.e(TAG, "Remote Message: " + e.getMessage());
//             PluginResult result = new PluginResult(PluginResult.Status.ERROR, "Some Error");

//         }
//     }

//     private void closeConnection(CallbackContext callbackContext) {
//         referrerClient.endConnection();
//     }

// }
