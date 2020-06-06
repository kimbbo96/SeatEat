package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.example.myapplication.db_obj.Restaurant;
import com.example.myapplication.utils.Cart;
import com.example.myapplication.utils.Utils;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ResDetail extends AppCompatActivity {
    private String path_base = "https://seateat-be.herokuapp.com";
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String urlBase = "https://seateat-be.herokuapp.com";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_detail);
        Toolbar toolbar = findViewById(R.id.tool_bar_simple);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark, this.getTheme()));


        final Restaurant rist = (Restaurant) getIntent().getSerializableExtra("Restaurant");
        System.out.println("RistID"+rist.getRESTAURANT_ID());
        System.out.println(rist.getRESTAURANT_TITLE());
        TextView nome_res = findViewById(R.id.res_name);
        nome_res.setText(rist.getRESTAURANT_TITLE());

        TextView tipo_res = findViewById(R.id.res_type);
        tipo_res.setText(rist.getRESTAURANT_TYPOLOGY());

        ImageView copertina = findViewById(R.id.copertina);
        String imgName = rist.getRESTAURANT_IMAGE();
        ProgressBar QRprogressBar = findViewById(R.id.progressBarQR);



        Activity activity = this;
        Glide.with(activity).load(Uri.parse(path_base+imgName)).into(new CustomViewTarget<ImageView, Drawable>(copertina) {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                copertina.setBackground(resource);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                Glide.with(activity).load(R.drawable.no_internet).into(copertina);
            }

            @Override
            protected void onResourceCleared(@Nullable Drawable placeholder) {}
        });

        ImageView qrImg = findViewById(R.id.imageView3);
        preferences = getSharedPreferences("infoRes", MODE_PRIVATE);
        TextView textQR = findViewById(R.id.textQR);
        Boolean isCapotavola = preferences.getBoolean("isCapotavola",false);
        String ResID = preferences.getString("ID","");
        ImageView cameraimg = findViewById(R.id.camera);
        TextView cameraInfo = findViewById(R.id.cameraInfo);
        System.out.println("id in preferences "+ResID);

        Button button = findViewById(R.id.menu_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("hai clikkato MENU");
                Intent intent = new Intent(ResDetail.this,FoodRest.class);
                intent.putExtra("Restaurant",rist); // passo l'oggetto ristornate
                startActivity(intent);
            }
        });

        if (ResID.equals("")) { // se non è impostato nessun ristorante allora genera il QR

            preferences = getSharedPreferences("loginref", MODE_PRIVATE);
            cameraimg.setVisibility(View.VISIBLE);
            cameraInfo.setVisibility(View.VISIBLE);

            String token = preferences.getString("nome", null) + ":" + preferences.getString("password", null);


            String BasicBase64format = "Basic " + Base64.getEncoder().encodeToString(token.getBytes());


            GlideUrl glideUrl = new GlideUrl(path_base + "/api/neworder/" + rist.getRESTAURANT_ID(), new LazyHeaders.Builder()
                    .addHeader("Authorization", BasicBase64format)
                    .build());

            Intent zoomQRIntent = new Intent(getApplicationContext(), Qr_zoom.class);
            Glide.with(this)
                    .asBitmap()
                    .load(glideUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .signature(new ObjectKey(System.currentTimeMillis()))
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            qrImg.setImageBitmap(resource);
                            qrImg.setVisibility(View.VISIBLE);
                            QRprogressBar.setVisibility(View.GONE);

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            resource.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            byte[] b = baos.toByteArray();
                            zoomQRIntent.putExtra("QRImage", b);
                            String imageEncoded = Base64.getEncoder().encodeToString(b);
                            System.out.println("QR codificato"+imageEncoded);

                            preferences = getSharedPreferences("infoRes", MODE_PRIVATE);

                            editor = preferences.edit();
                            editor.putString("QRimage", imageEncoded);
                            editor.commit();
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
            qrImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(zoomQRIntent);
                }
            });
        }

        else if(ResID.equals(rist.getRESTAURANT_ID()) && isCapotavola) { // se sono il capotavola e sono nel ristorante associato
            cameraimg.setVisibility(View.GONE);
            cameraInfo.setVisibility(View.GONE);
            System.out.println("schermata capotavola");
            String QREncoded = preferences.getString("QRimage",null);
            byte[] decodedByte = Base64.getDecoder().decode(QREncoded);
            Bitmap bmp = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
            qrImg.setImageBitmap(bmp); // setto il qr del capotavola salvato
            qrImg.setVisibility(View.VISIBLE);
            QRprogressBar.setVisibility(View.GONE);
            System.out.println("ho impostato il QR salvato");
            textQR.setText("Sei il capotavola. \n Fai scansionare questo QR ai tuoi amici. \n Buon appetito!");

            Intent zoomQRIntent = new Intent(getApplicationContext(), Qr_zoom.class);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] b = baos.toByteArray();
                    zoomQRIntent.putExtra("QRImage", b);
                }
            }).start();
            qrImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(zoomQRIntent);
                }
            });

        }
        else{ // associato con un altro ristorante
            cameraimg.setVisibility(View.GONE);
            cameraInfo.setVisibility(View.GONE);
            qrImg.setImageResource(R.drawable.noqr);
            qrImg.setVisibility(View.VISIBLE);
            QRprogressBar.setVisibility(View.GONE);
            textQR.setText("Sei già associato ad un altro ristorante. \n Buon appetito!");

        }

        RatingBar ratingBar = findViewById(R.id.ratingBar2);
        ratingBar.setEnabled(false);
        ratingBar.setRating(rist.getRESTAURANT_RATING());

        cameraimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(intent,0);
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
    }





    @Override
    public void onResume(){
        super.onResume();
        System.out.println("onrsume invocato");

        ProgressBar QRprogressBar = findViewById(R.id.progressBarQR);
        final Restaurant rist = (Restaurant) getIntent().getSerializableExtra("Restaurant");
        ImageView qrImg = findViewById(R.id.imageView3);
        preferences = getSharedPreferences("infoRes", MODE_PRIVATE);
        TextView textQR = findViewById(R.id.textQR);
        Boolean isCapotavola = preferences.getBoolean("isCapotavola",false);
        String ResID = preferences.getString("ID","");
        ImageView cameraimg = findViewById(R.id.camera);
        TextView cameraInfo = findViewById(R.id.cameraInfo);
        System.out.println("RESID su onresume --> "+ ResID);
         if(ResID.equals(rist.getRESTAURANT_ID()) && isCapotavola) { // se sono il capotavola e sono nel ristorante associato
            cameraimg.setVisibility(View.GONE);
            cameraInfo.setVisibility(View.GONE);
            System.out.println("schermata capotavola");
            String QREncoded = preferences.getString("QRimage",null);
            byte[] decodedByte = Base64.getDecoder().decode(QREncoded);
            Bitmap bmp = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
            qrImg.setImageBitmap(bmp); // setto il qr del capotavola salvato
            qrImg.setVisibility(View.VISIBLE);
            QRprogressBar.setVisibility(View.GONE);
            System.out.println("ho impostato il QR salvato");
            textQR.setText("Sei il capotavola. \n Fai scansionare questo QR ai tuoi amici. \n Buon appetito!");

            Intent zoomQRIntent = new Intent(getApplicationContext(), Qr_zoom.class);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] b = baos.toByteArray();
                    zoomQRIntent.putExtra("QRImage", b);
                }
            }).start();
            qrImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(zoomQRIntent);
                }
            });

        }
        else if (!isCapotavola && !ResID.equals("")){ // associato con un altro ristorante
            cameraimg.setVisibility(View.GONE);
            cameraInfo.setVisibility(View.GONE);
            qrImg.setImageResource(R.drawable.noqr);
            qrImg.setVisibility(View.VISIBLE);
            QRprogressBar.setVisibility(View.GONE);
            textQR.setText("Sei già associato ad un altro ristorante. \n Buon appetito!");

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null){
            if(result.getContents()== null){
                Toast.makeText(this,"you cancelled the scanning", Toast.LENGTH_LONG).show();
            }
            else{
                // invio lo scan al server
                String qr = result.getContents();
                Toast.makeText(this, qr,Toast.LENGTH_LONG).show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client = new OkHttpClient();
                        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
                        JSONObject qrData = new JSONObject();
                        try {
                            qrData.put("qrScanned",qr);
                        } catch (JSONException e) {
                            Log.d("OKHTTP3","JSON exception");
                            e.printStackTrace();
                        }

                        preferences = getSharedPreferences("loginref", MODE_PRIVATE);
                        String token = preferences.getString("nome", null) + ":" + preferences.getString("password", null);


                        String BasicBase64format = "Basic " + Base64.getEncoder().encodeToString(token.getBytes());

                        RequestBody body = RequestBody.create(JSON,qrData.toString());
                        Request newReq = new Request.Builder()
                                .url(path_base+"/api/scanassociationqr")
                                .post(body).addHeader("Authorization", BasicBase64format)
                                .build();


                        Response response = null;
                        JSONObject responsebody = null;
                        try {
                            response = client.newCall(newReq).execute();
                            responsebody = new JSONObject(response.body().string());

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("www"+response.message());
                        System.out.println("www"+response.isSuccessful());
                        System.out.println("kkk"+responsebody);

                        if (!response.isSuccessful()){
                            System.err.println("invio messaggio non riuscito");
                        }

                        else{ // ricevo il body con le info sul carrello?
                            preferences = getSharedPreferences("infoRes", MODE_PRIVATE);
                            editor = preferences.edit();
                            try {
                                SharedPreferences preferencesLogin = getSharedPreferences("loginref", MODE_PRIVATE);
                                String userId = preferencesLogin.getString("nome", "");
                                editor.putString("ID", (String) responsebody.get("id"));
                                editor.commit();
                                Cart cart = new Cart(getApplicationContext());
                                cart.load();
                                cart.addCartUser(userId, false); //utente aggiunto
                                JSONArray jsonArray = responsebody.getJSONArray("commensali");
                                cart.addCartUser((String) jsonArray.get(0), true); //utente capotavola
                                for (int i = 1 ; i < jsonArray.length();i++){
                                    cart.addCartUser((String) jsonArray.get(i), false); //utente aggiunto
                                    System.out.println("elemento"+jsonArray.get(i));
                                }
                                //cart.addCartUser(id, name, false); //utente aggiunto
                                cart.save();
                                Intent intent = new Intent(getApplicationContext(), FoodRest.class);
                                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                    }
                }).start();
            }
        }
        else {

            super.onActivityResult(requestCode, resultCode, data);
        }
    }









}