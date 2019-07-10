package com.example.myapplication.utils;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FireBaseService extends FirebaseMessagingService {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    String url = "https://seateat-be.herokuapp.com";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        System.out.println("collapseKey:"+remoteMessage.getCollapseKey());
        System.out.println("id:"+remoteMessage.getMessageId());
        System.out.println("getdata:"+remoteMessage.getData());
        String MessageType = remoteMessage.getData().get("type");

        switch (MessageType){ // per tutte le tipologie di notifiche che il server può mandare

            case "restaurantAssociation":{ //associazione al ristorante
                preferences = getSharedPreferences("infoRes", MODE_PRIVATE);
                editor = preferences.edit();
                editor.putString("ID", (remoteMessage.getData().get("ID")));
                if(remoteMessage.getData().get("isCapotavola").equals("True")){
                    editor.putBoolean("isCapotavola",true);
                }
                else {
                    editor.putBoolean("isCapotavola",false);
                }
                editor.commit();

            }

        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob();
            } else {
                // Handle message within 10 seconds
               // handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        OkHttpClient cl = new OkHttpClient(); // inizio la procedura di get
        Request request = new Request.Builder().url(url+"/api/testnotifications/"+token).build();
        System.out.println(url+"/api/testnotifications/"+token);
        cl.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.err.println("invio token fallito");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("invio token ok");
            }
        });

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(token);
    }

}