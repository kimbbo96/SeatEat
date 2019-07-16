package com.example.myapplication.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.myapplication.CartActivity;
import com.example.myapplication.Coll;
import com.example.myapplication.FoodRest;
import com.example.myapplication.MenuRest;
import com.example.myapplication.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Base64;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static xdroid.toaster.Toaster.toastLong;

public class FireBaseService extends FirebaseMessagingService {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    String url = "https://seateat-be.herokuapp.com";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        System.out.println("MESSAGE RECEIVED");

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        System.out.println("collapseKey:"+remoteMessage.getCollapseKey());
        System.out.println("id:"+remoteMessage.getMessageId());
        System.out.println("getdata:"+remoteMessage.getData());
        String MessageType = remoteMessage.getData().get("type");

        Context context = getBaseContext();
        preferences = getSharedPreferences("infoRes", MODE_PRIVATE);
        Cart cart = new Cart(context);

        switch (MessageType){ // per tutte le tipologie di notifiche che il server può mandare

            case "restaurantAssociation":{ //associazione al ristorante
                editor = preferences.edit();
                editor.putString("ID", (remoteMessage.getData().get("ID")));
                editor.putBoolean("isCapotavola",true);
                editor.commit();

                SharedPreferences preferencesUser = getSharedPreferences("loginref", MODE_PRIVATE);
                String userId = preferencesUser.getString("nome", "");
                cart.load();
                cart.addCartUser(userId, true); //utente aggiunto
                cart.save();

                Intent intent = new Intent(context, FoodRest.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                break;
            }

            case "userAssociation":{ // caso in cui al capotavola viene aggiunto un commensale
//                String id = remoteMessage.getData().get("id");
                String name = remoteMessage.getData().get("name");

                cart.load();
                cart.addCartUser(name, false); //utente aggiunto
                cart.save();

                Intent intent = new Intent("add_user");
                intent.putExtra("content", "new CartUser");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                break;
            }

            case "ordine_inviato": {

                cart.load();
                int lastOrdNum = Integer.valueOf(remoteMessage.getData().get("ord_num"));

                System.out.println("ORDINE INVIATO " + lastOrdNum);

                int currOrdNum = cart.getOrdNum();
                for (int i=0; i<lastOrdNum-currOrdNum; i++) {
                    cart.newOrder();
                }
                cart.save();

                Intent intent = new Intent("new_ord_num");
                intent.putExtra("content", "new NumOrd");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                break;
            }

            case "quota_inviata": {
                String user = remoteMessage.getData().get("user");
                double quantity = Double.valueOf(remoteMessage.getData().get("quantita"));

                System.out.println("QUOTA INVIATO " + user + " - " + quantity);

                cart.load();
                cart.setShare(user, quantity);
                cart.save();

                Intent intent = new Intent("new_share");
                intent.putExtra("content", "new Share");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                break;
            }

            case "triggercolletta": {
                System.out.println("TRIGGER COLLETTA");
                cart.shutDown();

                Intent intent = new Intent(context, Coll.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                toastLong("Il capotavola ha avviato la colletta");
                break;
            }

            case "triggerfatto": {
                System.out.println("TRIGGER FATTO");

                cart.clear();
                Utils.clearResPreferences(context);

                Intent intent = new Intent(context, MenuRest.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                toastLong("Grazie per aver usato la nostra app!");
                break;
            }

            case "triggerannulla": {
                System.out.println("TRIGGER ANNULLA");

                Intent intent = new Intent(context, CartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                toastLong("Il capotavola ha annullato la colletta");
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
