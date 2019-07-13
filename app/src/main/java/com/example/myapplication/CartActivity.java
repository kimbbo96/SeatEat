package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.utils.Cart;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

public class CartActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Cart cart;
    CartActivity activity;

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

        ViewPager viewPager = findViewById(R.id.viewPagerCart);
        TabLayout tabLayout = findViewById(R.id.tabLayoutCart);
        SimplePagerAdapter adapter = new SimplePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CartTabYou(activity), "I tuoi ordini");
        adapter.addFragment(new CartTabAll(activity), "Gli ordini di tutti");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
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

    class FabCartClickListener implements View.OnClickListener {
        Cart cart;

        public FabCartClickListener(Cart cart) {
            this.cart = cart;
        }

        @Override
        public void onClick(View view) {
            System.out.println("hai clikkato 'invia ordine'");
            cart.refresh();
            cart.newOrder();
            System.out.println("CARRELLO AGGIORNATO: " + cart);
            cart.save();
            finish();
//            Intent intent = new Intent(this, CartActivity.class);
//            intent.putExtra("Restaurant", rist); // passo l'oggetto ristornate
//            startActivity(intent);
        }
    }

}
