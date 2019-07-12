package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.utils.Cart;

public class Settings extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
}
