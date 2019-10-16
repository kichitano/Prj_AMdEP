package com.example.prj_amdep.Presentation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prj_amdep.Presentation.Fragment.EmergencyFragment;
import com.example.prj_amdep.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SOSActivity extends AppCompatActivity implements EmergencyFragment.OnFragmentInteractionListener{

    //VARIABLE DECLARATION
    public FirebaseUser firebaseUser;
    public FirebaseAuth firebaseAuth;
    private BroadcastReceiver broadcastReceiver;
    public String latitude = "", longitude = "";
    public DatabaseReference databaseReference;
    EmergencyFragment emergencyFragment;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Bundle extras = intent.getExtras();
                    longitude = extras.getString("Longitude");
                    latitude = extras.getString("Latitude");
                    //Toast.makeText(SOSActivity.this, latitude + " / " + longitude, Toast.LENGTH_SHORT).show();
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("locationUpdate"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
        //INITIALIZE FIREBASE AUTH
        firebaseAuth = FirebaseAuth.getInstance();
        //GET CURRENT USER
        firebaseUser = firebaseAuth.getCurrentUser();
        //USER TOAST
        Toast.makeText(getApplicationContext(), firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
        //GET DATABASE REFERENCE
        databaseReference = FirebaseDatabase.getInstance().getReference();
        //SET FRAGMENT ON ACTIVITY
        emergencyFragment = new EmergencyFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContanier, emergencyFragment).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }
}
