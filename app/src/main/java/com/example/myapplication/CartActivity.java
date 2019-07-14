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
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.utils.Cart;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
                cart.refresh();
                cart.newOrder();
                System.out.println("CARRELLO AGGIORNATO: " + cart);
                cart.save();
                finish();
//                Intent intent = new Intent(this, CartActivity.class);
//                intent.putExtra("Restaurant", rist); // passo l'oggetto ristornate
//                startActivity(intent);
            });
            fabCheckout.setOnClickListener(v -> {
                System.out.println("hai clikkato 'checkout'");

                Intent intent = new Intent(getApplicationContext(), Checkout.class);
//            intent.putExtra("Restaurant", rist);
                startActivity(intent);
                finish();
            });
        } else {
            fabCart.setEnabled(false);
            fabCheckout.setEnabled(false);
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
