package com.example.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.signature.ObjectKey;
import com.example.myapplication.utils.Cart;
import com.example.myapplication.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import android.util.Log;
import android.view.View;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.db_obj.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MenuRest extends AppCompatActivity {
    String urlBase = "https://seateat-be.herokuapp.com";
    static List<Restaurant> restaurants = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = this;
        restaurants.clear();
        setContentView(R.layout.activity_menu_rest);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        FloatingActionButton fab = findViewById(R.id.fab);
        ProgressBar progressBarResList = findViewById(R.id.progressBarResList);

        OkHttpClient cl = new OkHttpClient(); // inizio la procedura di get
        TextView errorMsg = findViewById(R.id.errorMessage);
        String url = urlBase+"/api/example/restaurants";
        Request request = new Request.Builder().url(url).build();
        cl.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                MenuRest.this.runOnUiThread(() -> {
                    progressBarResList.setVisibility(View.GONE);
                    errorMsg.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()){
                    final String muresponse = response.body().string();
                    MenuRest.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(muresponse);
                            try {
                                JSONArray jsonArray = new JSONArray(muresponse);
                                for (int i = 0 ; i< jsonArray.length(); i++){ // per ogni ristorante
                                    JSONObject jsonRes = jsonArray.getJSONObject(i) ;
                                    String nome = jsonRes.getString("nome");
                                    String id = jsonRes.getString("id");
                                    String typology = jsonRes.getString("tipologia");
                                    double rating = jsonRes.getDouble("rating");
                                    String pos = jsonRes.getString("posizione");
                                    String image = jsonRes.getString("immagine");
                                    Double x = Double.valueOf( pos.split(" ")[0]);
                                    Double y = Double.valueOf( pos.split(" ")[1]);
                                    Double[] pos_conv = {x,y};
                                    System.out.println("aasd"+jsonRes);

                                    Restaurant restaurant = new Restaurant(id,nome,typology,(float) rating,image,pos_conv);
                                    restaurants.add(restaurant);

                                }
                                System.out.println("okoko"+restaurants.size());

                                progressBarResList.setVisibility(View.GONE);
                                fillList(activity, restaurants);
                            }catch (JSONException err){
                                Log.d("Error", err.toString());
                            }
                        }
                    });
                }
            }
        });

        SharedPreferences preferences = getSharedPreferences("infoRes", MODE_PRIVATE);
        String ResID = preferences.getString("ID","");
        if (!ResID.equals("")){

            Cart cart = new Cart(this);

            Instant now = Instant.now();
            Instant start = cart.getTimestamp();

            Duration timeElapsed = Duration.between(start, now);
            if (timeElapsed.toHours() >= 3) { // if the last meal was >= 3 hours ago
                cart.clear(); // clean cart

            }
            else if(!cart.getCartFoods().isEmpty()){

                CharSequence message = "Sei attualmente associato ad un ristorante, vuoi proseguire con gli ordini?.";
                String posButtonText = "Termina ordinazione";
                DialogInterface.OnClickListener posButtonListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.out.println("ADDIO CARRELLOOOO!!!");
                        cart.clear();
                        SharedPreferences preferences = getSharedPreferences("infoRes", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("ID", "");
                        editor.commit();
                    }
                };
                String negButtonText = "Prosegui ordinazione";
                DialogInterface.OnClickListener negButtonListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent intent = new Intent(activity, FoodRest.class);
                        intent.putExtra("Restaurant", restaurants.get(Integer.parseInt(ResID)-1)); // passo l'oggetto ristorante
                        activity.startActivity(intent);

                    }
                };

                Utils.showDialog(activity, "Attenzione!", message, posButtonText, posButtonListener, negButtonText, negButtonListener);
            }


        }





    }

    static void fillList(Activity activity, List<Restaurant> restaurants) {
        ListView listView = activity.findViewById(R.id.list_view1);
        System.out.println(listView);

        RestListView customListView = new RestListView(activity, restaurants.toArray(new Restaurant[0]));

        System.out.println("aaaaaaaaaaas"+customListView+ listView);
        listView.setAdapter(customListView);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            System.out.println("hai clikkato "+i);
            SharedPreferences preferencesRest = activity.getSharedPreferences("infoRes", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferencesRest.edit();
            editor.putString("ID", String.valueOf(i+1));
            editor.commit();
            Intent intent = new Intent(activity, FoodRest.class);
            intent.putExtra("Restaurant", restaurants.get(i)); // passo l'oggetto ristorante
            activity.startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private boolean handleFilter() {
        BottomSheetDialogFragment filtersDialog = new FiltersDialogFragment();
        filtersDialog.show(getSupportFragmentManager(), "filtersDialog");

        return true;
    }

}
