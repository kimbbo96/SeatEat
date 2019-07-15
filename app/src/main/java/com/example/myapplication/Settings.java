package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.signature.ObjectKey;
import com.example.myapplication.utils.Cart;
import com.example.myapplication.utils.Utils;
import com.google.android.material.navigation.NavigationView;

public class Settings extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String urlBase = "https://seateat-be.herokuapp.com";



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.tool_bar_simple);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button logout_btn = findViewById(R.id.logout_btn);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // handle logout
                preferences = getSharedPreferences("loginref", MODE_PRIVATE);
                //preferences.
                editor = preferences.edit();
                editor.putString("nome", null);
                editor.putBoolean("savelogin", false);
                editor.putString("password",null);
                editor.putString("immagine",null);
                editor.commit();

                preferences = getSharedPreferences("infoRes", MODE_PRIVATE);
                editor = preferences.edit();
                editor.putString("QRimage",null);
                editor.putBoolean("isCapotavola",false);
                editor.putString("ID", "");
                editor.commit();

                // destroy cart
                Cart cart = new Cart(getApplicationContext());
                cart.clear();
                cart.save();

                Intent i = new Intent(Settings.this, Login.class);
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        preferences = getSharedPreferences("loginref", MODE_PRIVATE);
        TextView name_tab = findViewById(R.id.usr_name_tab);
        name_tab.setText( preferences.getString("nome", null));

        ImageView profile_image = findViewById(R.id.profile_image);
        System.out.println(urlBase+preferences.getString("immagine",null));
        RequestBuilder<Drawable> error = Glide.with(this).load(R.drawable.no_internet);
        Glide.with(this).load(urlBase+"/"+preferences.getString("immagine",null)).error(error)
                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                .fitCenter().into(profile_image);


        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rest, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Utils.gestisciMenu(item,this,findViewById(R.id.drawer_layout_settings));

        return true;
    }
}
