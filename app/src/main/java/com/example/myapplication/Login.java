package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
    String urlLogin = "https://seateat-be.herokuapp.com/api/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("loginref", MODE_PRIVATE);
        setContentView(R.layout.activity_login);
        Button button = findViewById(R.id.bLogin);
        EditText username = findViewById(R.id.etUsername);
        EditText paswd = findViewById(R.id.etPassword);
        TextView errmex = findViewById(R.id.err_message);

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
                            data.put("nickname",username.getText().toString());
                            data.put("password",paswd.getText().toString());
                            data.put("firebaseToken",
                                    preferences.getString("firebaseToken",null)); // invio il token di FireBase
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
                            JSONObject responsebody = new JSONObject(response.body().string());
                            System.out.println("www"+response.message());
                            System.out.println("www"+response.isSuccessful());

                            if (!response.isSuccessful()){

                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {

                                        try {
                                            System.out.println("òò"+responsebody.get("message")+ "Unauthorized");
                                            System.out.println("Unauthorized"==responsebody.get("message"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            if(responsebody.get("message").equals("Unauthorized")){
                                                errmex.setVisibility(View.VISIBLE);
                                                errmex.setText("id o password errati");
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });



                            }

                            else{
                                System.out.println(responsebody.toString());
                                editor = preferences.edit();
                                editor.putString("nome", (username.getText().toString()));
                                editor.putBoolean("savelogin", true);
                                editor.putString("password",paswd.getText().toString());
                                editor.putString("immagine",responsebody.get("immagine").toString());
                                editor.commit();
                                Intent i = new Intent(Login.this, MainActivity.class);
                                startActivity(i);
                                finish();}



                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("richiesta finita");

                    }
                }).start();

            }
        });
    }
}
