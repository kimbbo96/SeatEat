package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String urlLogin = "https://ptsv2.com/t/3efas-1561717656/post";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("loginref", MODE_PRIVATE);
        setContentView(R.layout.activity_login);
        Button button = findViewById(R.id.bLogin);
        EditText username = findViewById(R.id.etUsername);
        EditText paswd = findViewById(R.id.etPassword);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("cliccato");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("cliccatotr");

                        System.out.println((username.getText().toString()));


                        OkHttpClient client = new OkHttpClient();
                        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
                        JSONObject data = new JSONObject();
                        try {
                            data.put("nome",username.getText().toString());
                            data.put("password",paswd.getText().toString());
                        } catch (JSONException e) {
                            Log.d("OKHTTP3","JSON exception");
                            e.printStackTrace();
                        }
                        RequestBody body = RequestBody.create(JSON,data.toString());
                        Request newReq = new Request.Builder()
                                .url(urlLogin)
                                .post(body)
                                .build();
                        try {
                            Response response = client.newCall(newReq).execute();
                            System.out.println("www"+response.body().string());
                            System.out.println("www"+response.message());
                            System.out.println("www"+response.isSuccessful());


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("richiesta finita");


                        editor = preferences.edit();
                        editor.putString("nome", (username.getText().toString()));
                        editor.putBoolean("savelogin", true);
                        editor.putString("password",paswd.getText().toString());
                        editor.commit();
                        Intent i = new Intent(Login.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }).start();


            }
        });
    }
}
