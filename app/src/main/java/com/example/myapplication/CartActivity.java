package com.example.myapplication;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.utils.Cart;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

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

public class CartActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Cart cart;
    CartActivity activity;
    private final String POST_URL = "https://seateat-be.herokuapp.com/api/sendOrder";
    SharedPreferences preferencesLogin = activity.getSharedPreferences("loginref", MODE_PRIVATE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        cart = new Cart(activity);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = findViewById(R.id.tool_bar_simple);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // TODO gestire sto coso
        NavigationView navigationView = findViewById(R.id.nav_view_cart);
//        DrawerLayout drawer = findViewById(R.id.drawer_layout_cart);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                activity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(activity);

        String restId = getIntent().getStringExtra("RestId");
        ViewPager viewPager = findViewById(R.id.viewPagerCart);
        TabLayout tabLayout = findViewById(R.id.tabLayoutCart);
        SimplePagerAdapter adapter = new SimplePagerAdapter(getSupportFragmentManager());
        CartTabYou cty = new CartTabYou(activity, restId);
        CartTabAll cta = new CartTabAll(activity, restId);
        adapter.addFragment(cty, "I tuoi ordini");
        adapter.addFragment(cta, "Gli ordini di tutti");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        SharedPreferences preferences = activity.getSharedPreferences("infoRes", MODE_PRIVATE);
        boolean isCapotavola = preferences.getBoolean("isCapotavola",false);
        FloatingActionButton fabCart = activity.findViewById(R.id.fab_cart);
        FloatingActionButton fabCheckout = activity.findViewById(R.id.fab_checkout);
        if (isCapotavola) {
            fabCart.setOnClickListener(v -> {
                System.out.println("hai clikkato 'invia ordine'");
                new Thread( () -> cart.refresh());
                cart.newOrder();
                int lasOrdNum = cart.getOrdNum();
                System.out.println("CARRELLO AGGIORNATO: " + cart);
                cart.save();
                finish();




                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
                        OkHttpClient cl = new OkHttpClient(); // inizio la procedura di get

                        JSONObject dataUp = new JSONObject();
                        try {
                            dataUp.put("ord_num", lasOrdNum);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        RequestBody bodyUp = RequestBody.create(JSON, dataUp.toString());
                        String credenziali = preferencesLogin.getString("nome", null) + ":" + preferences.getString("password", null);
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


//                Intent intent = new Intent(this, CartActivity.class);
//                intent.putExtra("Restaurant", rist); // passo l'oggetto ristornate
//                startActivity(intent);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

            });






            fabCheckout.setOnClickListener(v -> {
                System.out.println("hai clikkato 'checkout'");

                Intent intent = new Intent(getApplicationContext(), Checkout.class);
//            intent.putExtra("Restaurant", rist);
                startActivity(intent);
                finish();
            });
        } else {
            fabCart.setVisibility(View.GONE);
            fabCheckout.setVisibility(View.GONE);
        }

//        tabLayout.addOnTabSelectedListener(
//                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
//                    @Override
//                    public void onTabSelected(TabLayout.Tab tab) {
//                        super.onTabSelected(tab);
//                        int numTab = tab.getPosition();
//                        if (numTab == 0) {
//                            cty.showButtons();
//                        } else if (numTab == 1) {
//                            cta.showButtons();
//                        }
//                        System.out.println("TAB selected " + numTab);
//                    }
//
//                    @Override
//                    public void onTabUnselected(TabLayout.Tab tab) {
//                        super.onTabUnselected(tab);
//                        int numTab = tab.getPosition();
//                        if (numTab == 0) {
//                            cta.hideButtons();
//                        } else if (numTab == 1) {
//                            cty.hideButtons();
//                        }
//                        System.out.println("TAB unselected " + numTab);
//                    }
//
//                    @Override
//                    public void onTabReselected(TabLayout.Tab tab) {
//                        super.onTabReselected(tab);
//                        int numTab = tab.getPosition();
//                        if (numTab == 0) {
//                            cty.showButtons();
//                        } else if (numTab == 1) {
//                            cta.showButtons();
//                        }
//                        System.out.println("TAB reselected " + numTab);
//                    }
//                });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action

        }  else if (id == R.id.nav_help) {
            Intent intent = new Intent(getApplicationContext(), Help.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        else if (id == R.id.nav_settings){
            Intent intent = new Intent(getApplicationContext(), Settings.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_qr, menu);
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
        } else if (id == R.id.qr_button) {
            SharedPreferences preferences = getSharedPreferences("infoRes", MODE_PRIVATE);
            String QREncoded = preferences.getString("QRimage",null);
            byte[] decodedByte = Base64.getDecoder().decode(QREncoded);
            Bitmap bmp = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);

            Intent zoomQRIntent = new Intent(getApplicationContext(), Qr_zoom.class);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            zoomQRIntent.putExtra("QRImage", b);
            startActivity(zoomQRIntent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    class FabCartClickListener implements View.OnClickListener {
//        Cart cart;
//
//        public FabCartClickListener(Cart cart) {
//            this.cart = cart;
//        }
//
//        @Override
//        public void onClick(View view) {
//            System.out.println("hai clikkato 'invia ordine'");
//            cart.refresh();
//            cart.newOrder();
//            System.out.println("CARRELLO AGGIORNATO: " + cart);
//            cart.save();
//            finish();
////            Intent intent = new Intent(this, CartActivity.class);
////            intent.putExtra("Restaurant", rist); // passo l'oggetto ristornate
////            startActivity(intent);
//        }
//    }
//
//    class FabCheckoutClickListener implements View.OnClickListener {
//
//        @Override
//        public void onClick(View view) {
//            System.out.println("hai clikkato 'checkout'");
//
//            Intent intent = new Intent(getApplicationContext(), Checkout.class);
////            intent.putExtra("Restaurant", rist);
//            startActivity(intent);
//            finish();
//        }
//    }

}
