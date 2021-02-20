package com.supertridents.ecom.merchant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        mFirebaseAuth = FirebaseAuth.getInstance();

        SharedPreferences.Editor editor = getSharedPreferences(MainActivity.CATEGORIES,MODE_PRIVATE).edit();
        editor.putString(MainActivity.CATCOUNTER,"5");
        editor.apply();
        editor.commit();


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                if (firebaseUser !=null) {
                    //some user logged in
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    finish();
                } else {
                    SharedPreferences preferences = getSharedPreferences(MainActivity.CART,MODE_PRIVATE);
                    startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                }

            }
        },3000);
    }
}