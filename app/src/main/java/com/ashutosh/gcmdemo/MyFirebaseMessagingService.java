package com.ashutosh.gcmdemo;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Vostro-Daily on 9/10/2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        Log.e(TAG, "FCM Notification Message: " +
                remoteMessage.getNotification());
        Log.e(TAG, "FCM Data Body: " + remoteMessage.getNotification().getBody());
        Log.e(TAG, "FCM Data Message : " + remoteMessage.getData());
    }
}
