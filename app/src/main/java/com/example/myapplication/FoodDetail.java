package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.myapplication.db_obj.Food;
import com.example.myapplication.db_obj.Restaurant;


public class FoodDetail extends AppCompatActivity {
    private String path_base = "https://seateat-be.herokuapp.com/resources/menus/";
    // path_base + "/resources/menus/" + restId + "/" + foodImage[position]))

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);
        Toolbar toolbar = findViewById(R.id.tool_bar_simple);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Food food = (Food) getIntent().getSerializableExtra("Food");
        final String restID = (String) getIntent().getSerializableExtra("RestID");

        TextView foodName = findViewById(R.id.foodNameDetail);
        foodName.setText(food.getFOOD_TITLE());

        TextView foodDesc = findViewById(R.id.foodDesDetail);
        foodDesc.setText(food.getFOOD_SHORT_DESCR());

        TextView foodDetail = findViewById(R.id.foodDetail);
        foodDetail.setText(food.getFOOD_LONG_DESCR());

        TextView foodPriceDetail = findViewById(R.id.foodPriceDetail);
        foodPriceDetail.setText(food.getFOOD_PRICE() + "€");

        Activity activity = this;
        String imgName = food.getFOOD_IMAGE();
        ImageView copertina = findViewById(R.id.copertinaFoodDetail);
        String imgURL = path_base + restID + "/" + imgName;
        Glide.with(activity).load(Uri.parse(imgURL)).into(new CustomViewTarget<ImageView, Drawable>(copertina) {
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

        Button button = findViewById(R.id.aggiungiButtonDetail);
        button.setOnClickListener(v -> {
            EditText notesField = findViewById(R.id.foodNotesDetail);
            String notes = notesField.getText().toString();
            System.out.println("hai clikkato AGGIUNGI " + food.getFOOD_TITLE() + "(id " + food.getFOOD_ID() + ")\nNotes: '" + notes + "'");
            finish();

//            Intent intent = new Intent(ResDetail.this,FoodRest.class);
//            intent.putExtra("Restaurant",rist); // passo l'oggetto ristornate
//            startActivity(intent);
        });
    }
}
