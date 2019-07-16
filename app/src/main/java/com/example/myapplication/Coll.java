package com.example.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.myapplication.utils.Cart;
import com.example.myapplication.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Coll extends AppCompatActivity {

    Cart cart;
    Activity activity;
    boolean created = false;

    double myShare = 0;
    double price;

    private final String POST_URL = "https://seateat-be.herokuapp.com/api/colletta";    // post autenticazione nell'header con cart nel body
    private final String PAY_URL = "https://seateat-be.herokuapp.com/api/triggerfatto";
    private final String CANCEL_URL = "https://seateat-be.herokuapp.com/api/triggerannulla";
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


        int people = users.size();
        price = cart.getTotalCheckout();

        TextView counterPeople = findViewById(R.id.counterPeople);
        counterPeople.setText(String.valueOf(people));

        TextView totalText = findViewById(R.id.priceText);
        System.out.println("totalText: " + totalText);
        totalText.setText(new Formatter().format(Locale.ITALIAN, "%.2f€", price).toString());

        TextView counterPrice = findViewById(R.id.counterPrice);
        System.out.println("counterPrice: " + counterPrice);
        counterPrice.setText(new Formatter().format(Locale.ITALIAN, "%.2f€", price/people).toString());

        TextView nameText = findViewById(R.id.myName);
        nameText.setText("Quanto vuoi versare?");

        EditText editText = (EditText) findViewById(R.id.shareEditText);

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

                                myShare = Double.parseDouble(editText.getText().toString());

                                System.out.println("MY NEW SHARE IS "+myShare);

                                // aggiorna myShare nel carrello
                                cart.load();
                                cart.setShare(userName, myShare);
                                cart.save();

                                fillTotal();

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
        Button payButton = findViewById(R.id.pay_button);
        double total = cart.getTotalShares();
        if (total < price) {
            payButton.setEnabled(false);
            payButton.setBackgroundColor(getColor(R.color.grey));
            payButton.setElevation(0);
        } else {
            payButton.setEnabled(true);
            payButton.setBackgroundColor(getColor(R.color.colorPrimary));
            payButton.setElevation(6);
        }

        if (isCapotavola) {

            cancelButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    System.out.println("hai clikkato ANNULLA");

                    Intent intent = new Intent(activity, CartActivity.class);
                    startActivity(intent);
//                    onBackPressed();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OkHttpClient client = new OkHttpClient();
                            String token = preferencesLogin.getString("nome", null) + ":" + preferencesLogin.getString("password", null);
                            String basicBase64format = "Basic " + Base64.getEncoder().encodeToString(token.getBytes());

                            Request.Builder builder = new Request.Builder();
                            builder.url(CANCEL_URL);
                            builder.addHeader("Authorization", basicBase64format);
                            Request downloadReq = builder.build();

                            client.newCall(downloadReq).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {

                                    System.err.println("errore invio al server");
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    System.out.println("trigger del bottone annulla inviato con successo");
                                }
                            });
                        }
                    }).start();
                }
            });


            payButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    System.out.println("hai clikkato PAGA");

                    Intent intent = new Intent(activity, MenuRest.class);
                    startActivity(intent);
                    Toast.makeText(activity, "Grazie per aver usato la nostra app!", Toast.LENGTH_LONG).show();

                    // pulisci carrello e preferenze
                    cart.clear();
                    Utils.clearResPreferences(activity);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OkHttpClient client = new OkHttpClient();
                            String token = preferencesLogin.getString("nome", null) + ":" + preferencesLogin.getString("password", null);
                            String basicBase64format = "Basic " + Base64.getEncoder().encodeToString(token.getBytes());

                            Request.Builder builder = new Request.Builder();
                            builder.url(PAY_URL);
                            builder.addHeader("Authorization", basicBase64format);
                            Request downloadReq = builder.build();

                            client.newCall(downloadReq).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {

                                    System.err.println("errore invio al server");
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    System.out.println("trigger del bottone paga inviato con successo");
                                }
                            });
                        }
                    }).start();
                }
            });
        } else {
            cancelButton.setVisibility(View.GONE);
            payButton.setVisibility(View.GONE);
        }

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
        totalObtained.setText(new Formatter().format(Locale.ITALIAN, "%.2f€ su %.2f€", total, price).toString());

        Button payButton = findViewById(R.id.pay_button);
        if (total < price) {
            payButton.setEnabled(false);
            payButton.setBackgroundColor(getColor(R.color.grey));
            payButton.setElevation(0);
        } else {
            payButton.setEnabled(true);
            payButton.setBackgroundColor(getColor(R.color.colorPrimary));
            payButton.setElevation(6);
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

    @Override
    public void onResume() {
        super.onResume();
        fillList(activity);
        fillTotal();
        if (myShare != 0) {
            EditText et = findViewById(R.id.shareEditText);
            et.setText(new Formatter().format(Locale.ITALIAN, "%.2f", myShare).toString());
        }
    }

}