package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.example.myapplication.db_obj.Restaurant;

import android.net.Uri;
import java.net.URISyntaxException;

public class res_detail extends AppCompatActivity {

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

        Button button = findViewById(R.id.menu_button);
         button.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                 System.out.println("hai clikkato MENU");
                 Intent intent = new Intent(res_detail.this,food_rest.class);
                 intent.putExtra("Restaurant",rist); // passo l'oggetto ristornate
                 startActivity(intent);
             }
         });

        nome_res.setText(rist.getRESTAURANT_TITLE());

        ImageView qrImg = findViewById(R.id.imageView3);
        Glide.with(this).load("https://seateat-be.herokuapp.com/api/newqr")
                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                .fitCenter().into(qrImg);


        RatingBar ratingBar = findViewById(R.id.ratingBar2);
        ratingBar.setEnabled(false);
        ratingBar.setRating(rist.getPRODUCT_RATING());
    }
}
