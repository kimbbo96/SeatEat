package com.example.myapplication.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.myapplication.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Base64;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FireBaseService extends FirebaseMessagingService {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    String url = "https://seateat-be.herokuapp.com";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        System.out.println("MESSAGE RECEIVED");

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
                editor.putString("ID", (remoteMessage.getData().get("id")));
                if(remoteMessage.getData().get("isCapotavola").equals("true")){
                    editor.putBoolean("isCapotavola",true);
                }
                else {
                    editor.putBoolean("isCapotavola",false);
                }
                editor.commit();
                break;
            }

            case "userAssociation":{ // caso in cui al capotavola viene aggiunto un commensale
                Cart cart = new Cart(getApplicationContext());
                cart.load();
                cart.addCartUser(remoteMessage.getData().get("id"),
                        remoteMessage.getData().get("name"), false); //utente aggiunto
                cart.save();

                Activity a = (Activity) getApplicationContext();
                a.findViewById(R.id.fellowship_cart_all);
                break;
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

        preferences = getSharedPreferences("loginref", MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString("firebaseToken", token);
        editor.commit();

        boolean savelogin = preferences.getBoolean("savelogin", false);

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        JSONObject data = new JSONObject();

        if (savelogin){ // se l'utente era già loggato allora invio l'aggiornamento

            String credenziali = preferences.getString("nome", null) + ":" + preferences.getString("password", null);
            String BasicBase64format = "Basic " + Base64.getEncoder().encodeToString(credenziali.getBytes());

            try {
                data.put("firebaseToken",token);
            } catch (JSONException e) {
                Log.d("OKHTTP3","JSON exception");
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(JSON,data.toString());
            Request newReq = new Request.Builder()
                    .url(url+"/api/testnotificationss")
                    .post(body).addHeader("Authorization", BasicBase64format)
                    .build();

            Response response = null;
            try {
                response = client.newCall(newReq).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("www"+response.message());
            System.out.println("www"+response.isSuccessful());

            if (!response.isSuccessful()){
                System.err.println("invio messaggio non riuscito");
            }

        }

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(token);
    }

}
