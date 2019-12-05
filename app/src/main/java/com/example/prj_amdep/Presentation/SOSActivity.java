package com.example.prj_amdep.Presentation;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.prj_amdep.Model.UserModel;
import com.example.prj_amdep.Presentation.Fragment.EmergencyFragment;
import com.example.prj_amdep.Presentation.Fragment.PreventMapFragment;
import com.example.prj_amdep.Presentation.Fragment.ProfileFragment;
import com.example.prj_amdep.Presentation.Fragment.PublicationsFragment;
import com.example.prj_amdep.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SOSActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //VARIABLE DECLARATION
    private NavigationView navigationView;
    private View headerView;
    private ImageView userPhoto;
    private TextView userNickname;
    private TextView userEmail;
    public FirebaseUser firebaseUser;
    public FirebaseAuth firebaseAuth;
    private BroadcastReceiver broadcastReceiver;
    public String latitude = "", longitude = "";
    public DatabaseReference databaseReference;
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private DatabaseReference mDatabase;
    public UserModel userModel;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReferenceProfilePic;

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
        //SET TOOLBAR
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navigationView.setItemIconTintList(null);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_emergencycall, R.id.nav_preventmap,
                R.id.nav_publications, R.id.nav_editprofile, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.fragmentContanier);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        //INITIALIZE FIREBASE AUTH
        firebaseAuth = FirebaseAuth.getInstance();
        //GET CURRENT USER
        firebaseUser = firebaseAuth.getCurrentUser();
        //USER TOAST
        //Toast.makeText(getApplicationContext(), firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
        //GET DATABASE REFERENCE
        databaseReference = FirebaseDatabase.getInstance().getReference();
        //STORAGE REFERENCE
        firebaseStorage = FirebaseStorage.getInstance();
        storageReferenceProfilePic = firebaseStorage.getReference();
        setNavigationViewListener();
        ///GET NAV VIEW
        navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        //SET COMPONENTS TO VARIABLES
        userPhoto = headerView.findViewById(R.id.UserPhoto);
        userNickname = headerView.findViewById(R.id.UserNickname);
        userEmail = headerView.findViewById(R.id.UserEmail);
        //GET USER
        userModel = new UserModel();
        getUser();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.fragmentContanier);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_emergencycall: {
                FrameLayout frameLayout = findViewById(R.id.fragmentContanier);
                frameLayout.removeAllViews();
                EmergencyFragment emergencyFragment = new EmergencyFragment();
                if(!(getCurrentFragment().equals(emergencyFragment))){
                    //GET THE FRAGMENT TO REPLACE ACTUAL
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    //PUT FRAGMENT IN CONTAINER
                    fragmentTransaction.replace(R.id.fragmentContanier, emergencyFragment);
                    fragmentTransaction.addToBackStack(null);
                    //COMMIT TRANSACTION
                    fragmentTransaction.commit();
                }
                break;
            }
            case R.id.nav_preventmap:{
                FrameLayout frameLayout = findViewById(R.id.fragmentContanier);
                frameLayout.removeAllViews();
                PreventMapFragment preventMapFragment = new PreventMapFragment();
                if(!(getCurrentFragment().equals(preventMapFragment))){
                    //GET THE FRAGMENT TO REPLACE ACTUAL
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    //PUT FRAGMENT IN CONTAINER
                    fragmentTransaction.replace(R.id.fragmentContanier, preventMapFragment);
                    fragmentTransaction.addToBackStack(null);
                    //COMMIT TRANSACTION
                    fragmentTransaction.commit();
                }
                break;
            }
            case R.id.nav_publications:{
                FrameLayout frameLayout = findViewById(R.id.fragmentContanier);
                frameLayout.removeAllViews();
                PublicationsFragment publicationsFragment = new PublicationsFragment();
                if(!(getCurrentFragment().equals(publicationsFragment))){
                    //GET THE FRAGMENT TO REPLACE ACTUAL
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    //PUT FRAGMENT IN CONTAINER
                    fragmentTransaction.replace(R.id.fragmentContanier, publicationsFragment);
                    fragmentTransaction.addToBackStack(null);
                    //COMMIT TRANSACTION
                    fragmentTransaction.commit();
                }
                break;
            }
            case R.id.nav_editprofile:{
                FrameLayout frameLayout = findViewById(R.id.fragmentContanier);
                frameLayout.removeAllViews();
                ProfileFragment profileFragment = new ProfileFragment();
                if(!(getCurrentFragment().equals(profileFragment))){
                    //GET THE FRAGMENT TO REPLACE ACTUAL
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    //PUT FRAGMENT IN CONTAINER
                    fragmentTransaction.replace(R.id.fragmentContanier, profileFragment);
                    fragmentTransaction.addToBackStack(null);
                    //COMMIT TRANSACTION
                    fragmentTransaction.commit();
                }
                break;
            }

            case R.id.nav_logout: {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(), R.string.exitSession, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SOSActivity.this, LoginActivity.class));
                finish();
                break;
            }
        }
        //close navigation drawer
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    Fragment getCurrentFragment()
    {
        Fragment currentFragment = getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContanier);
        return currentFragment;
    }

    private void getUser(){

        final ProgressDialog dialog = new ProgressDialog(SOSActivity.this);
        dialog.setMessage(getBaseContext().getResources().getString(R.string.Processing));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        userModel.setUserID(mDatabase.getKey());
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    userModel.setUserDNI(dataSnapshot.child("userDNI").getValue().toString());
                    userModel.setUserEmail(dataSnapshot.child("userEmail").getValue().toString());
                    userModel.setUserLastname(dataSnapshot.child("userLastname").getValue().toString());
                    userModel.setUserName(dataSnapshot.child("userName").getValue().toString());
                    userModel.setUserNickname(dataSnapshot.child("userNickname").getValue().toString());
                    userModel.setUserPassword(dataSnapshot.child("userPassword").getValue().toString());
                    userModel.setUserPhone(dataSnapshot.child("userPhone").getValue().toString());
                    //SET CONTENT ON VIEW
                    userNickname.setText(userModel.getUserNickname());
                    userEmail.setText(userModel.getUserEmail());
                    //GET USER PHOTO
                    StorageReference picReference = storageReferenceProfilePic.child("images/users/" + userModel.getUserID() + ".jpg");
                    final long ONE_MEGABYTE = 1024 * 1024;
                    picReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inMutable = true;
                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                            userPhoto.setImageBitmap(bmp);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                    dialog.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
