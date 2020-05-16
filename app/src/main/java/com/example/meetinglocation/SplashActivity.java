package com.example.meetinglocation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends Activity {
    private FirebaseAuth mAuth = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            Thread.sleep(2000); // 대기 초 설정
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Firebase 기존유저인지 검토 여부
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user!= null){
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
        else{
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
        finish();
    }
}
