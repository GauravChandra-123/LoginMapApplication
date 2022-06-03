package com.example.loginmapapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private EditText email,password;
    private Button btn1, btn2;
    float v = 0;
    private TextView txtView1;
    private final String CREDENTIAL_SHARED_PREF = "our_shared_pref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPass);
        btn1 = findViewById(R.id.img_login);
        txtView1 = findViewById(R.id.textView5);
        btn2 = findViewById(R.id.btnSignup);




        email.setTranslationY(800);
        password.setTranslationY(800);
        btn1.setTranslationY(800);
        btn2.setTranslationY(800);



        txtView1.setTranslationY(300);


        email.setAlpha(v);
        password.setAlpha(v);
        btn1.setAlpha(v);
        btn2.setAlpha(v);



        txtView1.setAlpha(v);

        email.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(800).start();
        password.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(800).start();
        btn1.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(800).start();
        btn2.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(800).start();




        txtView1.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(800).start();

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences credentials = getSharedPreferences(CREDENTIAL_SHARED_PREF, Context.MODE_PRIVATE);
                String user_name = credentials.getString("Username", null);
                String password_user = credentials.getString("Password", null);
                String username = email.getText().toString();
                String password1 = password.getText().toString();

                if (user_name != null && username != null && username.equalsIgnoreCase(username)){
                    if (password_user != null && password1 != null && password1.equalsIgnoreCase(password1)){
                        Toast.makeText(MainActivity.this, "Logging In", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, MapEditor.class);
                        startActivity(intent);
                    }
                    else{
                        alert("Sign up before Logging In!");

                    }
                } else {
                    alert("Sign up before Logging In!");
                }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, SignUp.class);
                startActivity(intent);

            }
        });


    }

    private void alert(String message){
        AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("Message").setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent intent = new Intent(MainActivity.this, SignUp.class);
                        startActivity(intent);
                    }
                })
                .create();
        dlg.show();
    }
}