package com.example.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.myapplication.db_obj.Food;
import com.example.myapplication.db_obj.Restaurant;
import com.example.myapplication.utils.Cart;
import com.example.myapplication.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;


public class Coll extends AppCompatActivity {

    Cart cart;
    Activity activity;
    boolean created = false;

    double myShare = 0;
    double totalShares = 0;
    double price;

    String urlBase = "https://seateat-be.herokuapp.com";

    private final String POST_URL = "https://seateat-be.herokuapp.com/api/colletta";    // post autenticazione nell'header con cart nel body

    static List<Cart.CartUser> users = new ArrayList<>();

    private BroadcastReceiver receiverShare = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && created) {
                String content = intent.getStringExtra("content");
                System.out.println(content + "coll");
                if (content.equals("new Share")) {
                    fillList(activity);
                    fillTotal();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        created = true;

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(activity);
        lbm.registerReceiver(receiverShare, new IntentFilter("new_share"));

        SharedPreferences preferences = activity.getSharedPreferences("infoRes", MODE_PRIVATE);
        SharedPreferences preferencesLogin = activity.getSharedPreferences("loginref", MODE_PRIVATE);
        boolean isCapotavola = preferences.getBoolean("isCapotavola",false);
        String userName = preferencesLogin.getString("nome", "");

        setContentView(R.layout.activity_coll);
        Toolbar toolbar = findViewById(R.id.tool_bar_simple);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cart = new Cart(activity);
        cart.load();
        myShare = cart.getShare(userName);

        users.clear();

        // riempi lista users

        users.addAll(cart.getCartUsers());

        System.out.println("CART USERS: "+cart.getCartUsersNames());

//        for (Cart.CartUser u : users) {
//            u.setShare(10.0); //dovremmo mettere quanto ricevuto dalla notifica
//
//            totalShares += u.getShare();
//        }

        int people = users.size();
        price = cart.getTotalCheckout();

        TextView counterPeople = findViewById(R.id.counterPeople);
        counterPeople.setText(String.valueOf(people));

        TextView totalText = findViewById(R.id.priceText);
        System.out.println("totalText: " + totalText);
        totalText.setText(String.valueOf(price)+"€");

        TextView counterPrice = findViewById(R.id.counterPrice);
        System.out.println("counterPrice: " + counterPrice);
        counterPrice.setText(String.valueOf(price/people)+"€");

        TextView nameText = findViewById(R.id.myName);
        nameText.setText("Quanto vuoi versare?");

        EditText editText = (EditText) findViewById(R.id.shareEditText);
        /*editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 0) {
                    myShare = Double.parseDouble(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

//        totalShares += myShare;

        editText.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        System.out.println("oneditoractionlistener - id=" + actionId + ", event=" + event);

                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event != null &&
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (event == null || !event.isShiftPressed()) {
                                // the user is done typing.

                                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

//                                totalShares -= myShare;

                                myShare = Double.parseDouble(editText.getText().toString());

                                System.out.println("MY NEW SHARE IS "+myShare);

//                                totalShares += myShare;

                                // aggiorna myShare nel carrello
                                cart.load();
                                cart.setShare(userName, myShare);
                                cart.save();

                                new Thread(() -> {

                                    OkHttpClient cl = new OkHttpClient(); // inizio la procedura di get

                                    MediaType JSON = MediaType.parse("application/json;charset=utf-8");
                                    String credenziali = preferencesLogin.getString("nome", null) + ":" + preferencesLogin.getString("password", null);
                                    String BasicBase64format = "Basic " + Base64.getEncoder().encodeToString(credenziali.getBytes());

                                    JSONObject colletta = new JSONObject();
                                    try {
                                        colletta.put("quantita", myShare);
                                    } catch (JSONException e) {
                                        Log.d("OKHTTP3", "JSON exception");
                                        e.printStackTrace();
                                    }

                                    RequestBody bodyUp = RequestBody.create(JSON, colletta.toString());

                                    Request uploadReq = new Request.Builder()
                                            .url(POST_URL)
                                            .post(bodyUp)
                                            .addHeader("Authorization", BasicBase64format)
                                            .build();
                                    try {
                                        Response response = cl.newCall(uploadReq).execute();

                                        if (response.isSuccessful()) {
                                            System.out.println("COLLETTA REQUEST post SUCCESSFUL" + response.message());
                                            System.out.println("COLLETTA REQUEST post SUCCESSFUL" + response.isSuccessful());
                                        } else {
                                            System.out.println("COLLETTA REQUEST post UNSUCCESSFUL" + response.message());
                                            System.out.println("COLLETTA REQUEST post UNSUCCESSFUL" + response.isSuccessful());
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }).start();

                                return true; // consume.
                            }
                        }
                        return false; // pass on to other listeners.
                    }
                }
        );

        fillTotal();

        Button cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("hai clikkato ANNULLA");
                /*Intent intent = new Intent(ResDetail.this,FoodRest.class);
                intent.putExtra("Restaurant",rist); // passo l'oggetto ristornate
                startActivity(intent);*/
            }
        });


        Button payButton = findViewById(R.id.pay_button);
        payButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("hai clikkato PAGA");
                /*Intent intent = new Intent(ResDetail.this,FoodRest.class);
                intent.putExtra("Restaurant",rist); // passo l'oggetto ristornate
                startActivity(intent);*/
            }
        });

        //costruisci CollListView
        fillList(activity);

    }

    private void fillList(Activity activity) {
        System.out.println("FILL LIST COLL");

        cart.load();
        List<Cart.CartUser> users = cart.getCartUsers();

        ListView listView = activity.findViewById(R.id.list_view_coll);
        System.out.println(listView);

        SharedPreferences preferencesLogin = activity.getSharedPreferences("loginref", MODE_PRIVATE);
        String userName = preferencesLogin.getString("nome", "");

        for (Cart.CartUser c : users) {
            if (c.getName().equals(userName)) {
                users.remove(c);
                break;
            }
        }

        Cart.CartUser[] cartUsers = users.toArray(new Cart.CartUser[0]);

        CollListView customListView = new CollListView(activity, cartUsers);

        listView.setAdapter(customListView);

        Utils.justifyListViewHeight(listView);
    }

    private void fillTotal() {
        cart.load();
        double total = cart.getTotalShares();
        System.out.println("fill total: " + total);
        TextView totalObtained = findViewById(R.id.counterTotal);
        totalObtained.setText(total + "€ su " + price + "€");
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
    public void onResume() {
        super.onResume();
        fillList(activity);
        fillTotal();
    }

}