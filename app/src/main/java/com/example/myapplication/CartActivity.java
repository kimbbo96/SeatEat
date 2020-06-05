package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.utils.Cart;
import com.example.myapplication.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CartActivity extends AppCompatActivity {
    Cart cart;
    CartActivity activity;
    private final String POST_URL = "https://seateat-be.herokuapp.com/api/sendOrder";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        cart = new Cart(activity);
        cart.load();
        Toast.makeText(activity, "cart info: " + cart.getCartFoods().size() + " - " + cart.getTotal(), Toast.LENGTH_LONG).show(); // todo debug
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = findViewById(R.id.tool_bar_simple);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferencesRest = this.getSharedPreferences("infoRes", MODE_PRIVATE);
        String idRestPref = preferencesRest.getString("ID","");
        String rid = getIntent().getStringExtra("RestID");
        String restId = rid == null ? idRestPref : rid;
        ViewPager viewPager = findViewById(R.id.viewPagerCart);
        TabLayout tabLayout = findViewById(R.id.tabLayoutCart);
        SimplePagerAdapter adapter = new SimplePagerAdapter(getSupportFragmentManager());
        CartTabYou cty = new CartTabYou(activity, restId);
        adapter.addFragment(cty, "I tuoi ordini");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setActivated(false);

        SharedPreferences preferences = activity.getSharedPreferences("infoRes", MODE_PRIVATE);
        boolean isCapotavola = preferences.getBoolean("isCapotavola",false);
        FloatingActionButton fabCart = activity.findViewById(R.id.fab_cart);
        FloatingActionButton fabCheckout = activity.findViewById(R.id.fab_checkout);

        if (isCapotavola) {
            fabCart.setOnClickListener(v -> {
                System.out.println("hai clikkato 'invia ordine'");
                cart.newOrder();
                int lasOrdNum = cart.getOrdNum();
                System.out.println("CARRELLO AGGIORNATO: " + cart);
                cart.save();
                finish();

                new Thread(() -> {
                    MediaType JSON = MediaType.parse("application/json;charset=utf-8");
                    OkHttpClient cl = new OkHttpClient(); // inizio la procedura di get

                    JSONObject dataUp = new JSONObject();
                    try {
                        dataUp.put("ord_num", lasOrdNum);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    SharedPreferences preferencesLogin = activity.getSharedPreferences("loginref", MODE_PRIVATE);

                    RequestBody bodyUp = RequestBody.create(JSON, dataUp.toString());
                    String credenziali = preferencesLogin.getString("nome", null) + ":" + preferencesLogin.getString("password", null);

                    String BasicBase64format = "Basic " + Base64.getEncoder().encodeToString(credenziali.getBytes());

                    Request uploadReq = new Request.Builder()
                            .url(POST_URL)
                            .post(bodyUp)
                            .addHeader("Authorization", BasicBase64format)
                            .build();
                    try {
                        Response response = cl.newCall(uploadReq).execute();

                        if (response.isSuccessful()) {
                            System.out.println("NUOVO NUMERO DEL CARRELLO INVIATO CON SUCCESSO " + response.message());

                        } else {
                            System.out.println("INVIO NUMERO ORDINE UNSUCCESSFUL" + response.message());
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }).start();

            });
        } else {
            fabCart.setVisibility(View.GONE);
            fabCheckout.setVisibility(View.GONE);
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_cart);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
