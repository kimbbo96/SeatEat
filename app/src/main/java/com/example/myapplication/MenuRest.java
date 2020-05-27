package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.signature.ObjectKey;
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
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MenuRest extends AppCompatActivity
       {
    SharedPreferences preferences;


//    Restaurant r1 = new Restaurant("1","Trattoria IV Secolo",
//            "Trattoria, Italiano",(float)6,"image", new Double[] {42.002780, 12.384011});
//    Restaurant r2 = new Restaurant("2","Panizzeri",
//            "Panineria, Italiano",(float)4,"image", new Double[] {41.968043, 12.537057});
//    Restaurant r3 = new Restaurant("3","Ristorante Jinja",
//            "Giapponese, Cinese",(float)1,"image", new Double[] {41.927775, 12.480815});

    private static Restaurant r1 = new Restaurant("1","Trattoria IV Secolo",
            "Trattoria, Italiano",(float)6,"iv_secolo_logo", new Double[] {42.002780, 12.384011});
    private static Restaurant r2 = new Restaurant("2","Panizzeri",
            "Paninoteca, Italiano",(float)4,"panizzeri_logo", new Double[] {41.968043, 12.537057});
    private static Restaurant r3 = new Restaurant("3","Ristorante Jinja",
            "Giapponese, Cinese",(float)1,"jinja_logo", new Double[] {41.927775, 12.480815});

//    static Restaurant[] restaurants = {r1, r2, r3};
    String urlBase = "https://seateat-be.herokuapp.com";
    //static Restaurant[] restaurants;

    static List<Restaurant> restaurants = new ArrayList<>();

//    List<Restaurant> list = new ArrayList<>();

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
//                              restaurants =  restaurants.toArray(new Restaurant[0]);

//                                fab.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                                                .setAction("Action", null).show();
//                                    }
//                                });

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
    }

    static void fillList(Activity activity, List<Restaurant> restaurants) {
        ListView listView = activity.findViewById(R.id.list_view1);
        System.out.println(listView);

        RestListView customListView = new RestListView(activity, restaurants.toArray(new Restaurant[0]));

        System.out.println("aaaaaaaaaaas"+customListView+ listView);
        listView.setAdapter(customListView);
//        final List<Restaurant> resList = new ArrayList<>(Arrays.asList(restaurants));

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            System.out.println("hai clikkato "+i);
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
