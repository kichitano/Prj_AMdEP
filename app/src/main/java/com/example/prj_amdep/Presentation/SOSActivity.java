package com.example.prj_amdep.Presentation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.prj_amdep.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SOSActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onStart() {
        super.onStart();
        //CHECK LOGGIN AND USER DATA IS NON NULL
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        updateUI(firebaseUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        //INITIALIZE FIREBASE AUTH
        firebaseAuth = firebaseAuth.getInstance();

    }

    private void updateUI(FirebaseUser firebaseUser) {

    }
}
