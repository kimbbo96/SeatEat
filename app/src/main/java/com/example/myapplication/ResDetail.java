package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.example.myapplication.db_obj.Restaurant;

import java.lang.reflect.Field;

public class res_detail extends AppCompatActivity {
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
                Intent intent = new Intent(res_detail.this,food_rest.class);
                intent.putExtra("Restaurant",rist); // passo l'oggetto ristornate
                startActivity(intent);
            }
        });
    }
}
