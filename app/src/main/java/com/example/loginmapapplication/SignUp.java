package com.example.loginmapapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class SignUp extends AppCompatActivity {
    private EditText edtxt1,edtxt2,edtxt3,edtxt4, edtxt5;
    private final String CREDENTIAL_SHARED_PREF = "our_shared_pref";
    private Button btn, btn3;
    boolean isAllFieldChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtxt1 = findViewById(R.id.signEmail);
        edtxt2 = findViewById(R.id.signPass);
        edtxt3 = findViewById(R.id.signcnfPass);
        edtxt4 = findViewById(R.id.usr_address);
        edtxt5 = findViewById(R.id.usr_phone);
        btn = findViewById(R.id.img_signup);
        btn3 = findViewById(R.id.btnLogin);
        edtxt1.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(800).start();
        edtxt2.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(800).start();
        edtxt3.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(800).start();
        edtxt4.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(800).start();
        edtxt5.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(800).start();
        btn.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(800).start();
        btn3.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(800).start();



        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, MainActivity.class);
                startActivity(intent);
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usr_name = edtxt1.getText().toString();
                String str_password = edtxt2.getText().toString();
                String cnf_password = edtxt3.getText().toString();

                if (str_password != null && cnf_password != null && str_password.equalsIgnoreCase(cnf_password)){
                    SharedPreferences credentials = getSharedPreferences(CREDENTIAL_SHARED_PREF, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = credentials.edit();
                    editor.putString("Password", str_password);
                    editor.putString("Username", usr_name);
                    editor.commit();
                    checkDataInput();
                }
                isAllFieldChecked = checkDataInput();
                if(isAllFieldChecked){
                    SignUp.this.finish();
                }
            }
        });
    }

    boolean isEmail(EditText text){
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    boolean isEmpty(EditText text){
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    boolean checkDataInput(){
        if(isEmpty(edtxt1)){
            edtxt1.setError("Email Address Required");
            return false;
        }
        if (isEmpty(edtxt2)){
            edtxt2.setError("Password Required");
            return false;
        }
        if(isEmpty(edtxt3)){
            edtxt3.setError("Enter the Password Again");
            return false;
        }
        return true;
    }



}