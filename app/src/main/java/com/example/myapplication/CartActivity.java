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
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class CartActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = this;
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = findViewById(R.id.toolbar_cart);
        setSupportActionBar(toolbar);

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

        ListView listView = activity.findViewById(R.id.list_view_cart);
        Cart cart = new Cart(activity);
        cart.load();
        List<Cart.CartFood> foods = cart.getCartFoods();
        CartListView customListView = new CartListView(activity, foods);
        listView.setAdapter(customListView);

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
}
