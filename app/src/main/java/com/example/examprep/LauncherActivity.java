package com.example.examprep;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;


public class LauncherActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("LauncherActivity", "User signed in? " + (user != null));



        Intent intent;
        if(user != null){
            intent = new Intent(this, HomeActivity.class);

        }else{
            intent = new Intent(this, PhoneAuthActivity.class);

        }
        startActivity(intent);
        finish();
    }
}