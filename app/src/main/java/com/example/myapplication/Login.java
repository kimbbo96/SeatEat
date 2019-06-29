package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

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
                System.out.println((username.getText().toString()));

                editor = preferences.edit();
                editor.putString("nome", (username.getText().toString()));
                editor.putBoolean("savelogin", true);
                editor.putString("password",paswd.getText().toString());
                editor.commit();
                Intent i = new Intent(Login.this, MainActivity.class);
                startActivity(i);
                finish();

            }
        });
    }
}
