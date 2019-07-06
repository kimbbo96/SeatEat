package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.myapplication.utils.Cart;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Cart cart = new Cart(this);
    ListView listView;
    ListView listViewOld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = this;
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
        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) activity);
        ProgressBar progressBarCart = findViewById(R.id.progressBar_cart);
        progressBarCart.setVisibility(View.GONE);

        listView = activity.findViewById(R.id.list_view_cart);
        cart = cart.load();
        System.out.println("CARRELLO ATTUALE: " + cart);
        int ordNum = cart.getOrdNum();
        System.out.println("ordNum: " + ordNum);
        List<Cart.CartFood> foods = cart.getCartFoods(ordNum);
        CartListView customListView = new CartListView(activity, foods);
        listView.setAdapter(customListView);

        listViewOld = activity.findViewById(R.id.list_view_cart_old);
        List<Cart.CartFood> foodsOld = new ArrayList<>();
        for (int i = 1; i < ordNum; i++) {
            foodsOld.addAll(cart.getCartFoods(i));
        }
        CartListView customListViewOld = new CartListView(activity, foodsOld);
        listViewOld.setAdapter(customListViewOld);

        ExtendedFloatingActionButton fab = findViewById(R.id.fab_cart);
        fab.setOnClickListener(view -> {
            System.out.println("hai clikkato 'invia ordine'");
            cart.newOrder();
            System.out.println("CARRELLO AGGIORNATO: " + cart);
            cart.save();
            finish();
//            Intent intent = new Intent(this, CartActivity.class);
//            intent.putExtra("Restaurant", rist); // passo l'oggetto ristornate
//            startActivity(intent);
        });

//        final List<Restaurant> resList = new ArrayList<>(Arrays.asList(restaurants));
//        listView.setOnItemClickListener((adapterView, view, i, l) -> {
//            System.out.println("hai clikkato "+i);
//            Intent intent = new Intent(activity, ResDetail.class);
//            intent.putExtra("Restaurant", restaurants.get(i)); // passo l'oggetto ristorante
//            activity.startActivity(intent);
//        });
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
