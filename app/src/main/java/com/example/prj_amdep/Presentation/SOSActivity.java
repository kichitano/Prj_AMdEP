package com.example.prj_amdep.Presentation;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prj_amdep.Presentation.Fragment.EmergencyFragment;
import com.example.prj_amdep.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SOSActivity extends AppCompatActivity implements EmergencyFragment.OnFragmentInteractionListener{

    //VARIABLE DECLARATION
    private FirebaseAuth firebaseAuth;
    EmergencyFragment emergencyFragment;

    @Override
    protected void onStart() {
        super.onStart();
        //CHECK LOGGIN AND USER DATA IS NON NULL
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        //BINDING SERVICE
/*        final Intent serviceStart = new Intent(this.getApplication(), LocationService.class);
        this.getApplication().startService(serviceStart);
        this.getApplication().bindService(serviceStart, serviceConnection, Context.BIND_AUTO_CREATE);*/

        //SET FRAGMENT ON ACTIVITY
        emergencyFragment = new EmergencyFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContanier, emergencyFragment).commit();

        //INITIALIZE FIREBASE AUTH
        firebaseAuth = firebaseAuth.getInstance();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
