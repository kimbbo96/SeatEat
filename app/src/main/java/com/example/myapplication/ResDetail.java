package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.example.myapplication.db_obj.Restaurant;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.lang.reflect.Field;

public class ResDetail extends AppCompatActivity {
    private String path_base = "https://seateat-be.herokuapp.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_detail);
        Toolbar toolbar = findViewById(R.id.tool_bar_simple);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("SeatEat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Restaurant rist = (Restaurant) getIntent().getSerializableExtra("Restaurant");
        System.out.println(rist.getRESTAURANT_TITLE());
        TextView nome_res = findViewById(R.id.res_name);
        nome_res.setText(rist.getRESTAURANT_TITLE());

        TextView tipo_res = findViewById(R.id.res_type);
        tipo_res.setText(rist.getRESTAURANT_TYPOLOGY());

        ImageView copertina = findViewById(R.id.copertina);
        String imgName = rist.getRESTAURANT_IMAGE();

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
        RequestBuilder<Drawable> error = Glide.with(this).load(R.drawable.no_internet);
        Glide.with(this).load(path_base+"/api/newqr").error(error)
                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                .fitCenter().into(qrImg);

        RatingBar ratingBar = findViewById(R.id.ratingBar2);
        ratingBar.setEnabled(false);
        ratingBar.setRating(rist.getRESTAURANT_RATING());

        Button button = findViewById(R.id.menu_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("hai clikkato MENU");
                Intent intent = new Intent(ResDetail.this,FoodRest.class);
                intent.putExtra("Restaurant",rist); // passo l'oggetto ristornate
                startActivity(intent);
            }
        });
        ImageView cameraimg = findViewById(R.id.camera);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null){
            if(result.getContents()== null){
                Toast.makeText(this,"you cancelled the scanning", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(this, result.getContents(),Toast.LENGTH_LONG).show();

            }
        }
        else {

            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}