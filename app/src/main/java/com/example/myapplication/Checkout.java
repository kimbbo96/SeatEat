package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.myapplication.db_obj.Food;
import com.example.myapplication.db_obj.Restaurant;
import com.example.myapplication.utils.Cart;
import com.example.myapplication.utils.Utils;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.Base64;
import java.util.Formatter;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Checkout extends AppCompatActivity {
    Context context;
    Cart cart;


    int people = 0;
    double price = 0;
    private final String GET_CHECKOUT_OUT = "https://seateat-be.herokuapp.com/api/triggercolletta";
    private final String PAY_URL = "https://seateat-be.herokuapp.com/api/triggerfatto";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        cart = new Cart(this);

        setContentView(R.layout.activity_checkout);
        Toolbar toolbar = findViewById(R.id.tool_bar_simple);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cart.load();

        people = cart.getCartUsers().size();

        TextView counterPeople = findViewById(R.id.counterPeople);
        counterPeople.setText(String.valueOf(people));

        price = cart.getTotalCheckout();

        TextView totalText = findViewById(R.id.priceText);
        System.out.println("totalText: " + totalText);
        totalText.setText(new Formatter().format(Locale.ITALIAN, "%.2f€", price).toString());

        TextView counterPrice = findViewById(R.id.counterPrice);
        System.out.println("counterPrice: " + counterPrice);
        counterPrice.setText(new Formatter().format(Locale.ITALIAN, "%.2f€", price/people).toString());



        // add people
//        ImageButton addIB = findViewById(R.id.addPeople);
//        addIB.setOnClickListener(view -> {
//            people++;
//            counterPeople.setText(Integer.toString(people));
//            counterPrice.setText(new Formatter().format(Locale.ITALIAN, "%.2f€", price/people).toString());
//        });

        // remove people
//        ImageButton remIB = findViewById(R.id.removePeople);
//        remIB.setOnClickListener(view -> {
//            if (people > 0) {
//                people--;
//                counterPeople.setText(Integer.toString(people));
//                counterPrice.setText(new Formatter().format(Locale.ITALIAN, "%.2f€", price/people).toString());
//            }
//        });
    }

    private DialogInterface.OnClickListener payWithCashListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            cart.clear();
            Utils.clearResPreferences(context);

            Intent intent = new Intent(context, MenuRest.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Toast.makeText(context, "Grazie per aver usato la nostra app!", Toast.LENGTH_LONG).show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    OkHttpClient client = new OkHttpClient();
                    SharedPreferences preferencesLogin = getSharedPreferences("loginref", MODE_PRIVATE);
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
    };

    private DialogInterface.OnClickListener payWithCollListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            Intent intent = new Intent(context, Coll.class);
            SharedPreferences preferencesLogin = getSharedPreferences("loginref", MODE_PRIVATE);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    OkHttpClient client = new OkHttpClient();
                    String token = preferencesLogin.getString("nome", null) + ":" + preferencesLogin.getString("password", null);
                    String basicBase64format = "Basic " + Base64.getEncoder().encodeToString(token.getBytes());

                    Request.Builder builder = new Request.Builder();
                    builder.url(GET_CHECKOUT_OUT);
                    builder.addHeader("Authorization", basicBase64format);
                    Request downloadReq = builder.build();

                    client.newCall(downloadReq).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                            System.err.println("errore invio al server");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            System.out.println("trigger della colletta inviato con successo");
                        }
                    });
                }
            }).start();

//        //  fare in modo di passare il campo people aggiornato
//
//        intent.putExtra("People", people); // passo il numero di commensali
//        intent.putExtra("Price", price); // passo il prezzo totale

            startActivity(intent);
        }
    };

    public void payWithCash(View view) {
        System.out.println("hai clikkato CASH");

        Utils.showDialog(this, "Paga in contanti",
                "Vai alla cassa.\nAndando avanti terminerai il pasto e non potrai aggiungere nuovi ordini",
                "Conferma", payWithCashListener, "Annulla", null);
    }

    public void payWithCard(View view) {
        System.out.println("hai clikkato CARD");

        Utils.showDialog(this, "Paga con l'app",
                "Pagherai con il conto associato all'app.\nAndando avanti terminerai il pasto e non potrai aggiungere nuovi ordini",
                "Conferma", payWithCashListener, "Annulla", null);
    }

    public void payWithColl(View view) {
        System.out.println("hai clikkato COLL");

        Utils.showDialog(this, "Paga facendo una colletta nell'app",
                "Andando avanti terminerai il pasto e non potrai aggiungere nuovi ordini",
                "Conferma", payWithCollListener, "Annulla", null);
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