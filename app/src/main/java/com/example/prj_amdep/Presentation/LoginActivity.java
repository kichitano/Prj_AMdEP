package com.example.prj_amdep.Presentation;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.prj_amdep.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    //VARIABLES DECLARATION
    private int INTERNET_PERMISSION_CODE = 1;
    private int LOCATION_PERMISSION_CODE = 2;
    private int PHONE_PERMISSION_CODE = 3;
    private int SENDSMS_PERMISSION_CODE = 4;
    private int RECEIVESMS_PERMISSION_CODE = 5;
    private Button btnLoginUser;
    private Button btnSignupUser;
    private EditText txtEmailUser;
    private EditText txtPasswordUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            startActivity(new Intent(LoginActivity.this, SOSActivity.class));
            finish();
        } else {
            //SET CONTENT VIEW
            setContentView(R.layout.activity_login);
            //CHECK PERMISSIONS
            if(ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(LoginActivity.this, "You already granted permisssion.", Toast.LENGTH_SHORT).show();
            }else{
                RequestInternetPermission();
            }
            if(ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(LoginActivity.this, "You already granted permisssion.", Toast.LENGTH_SHORT).show();
            }else{
                RequestLocationPermission();
            }
            if(ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(LoginActivity.this, "You already granted permisssion.", Toast.LENGTH_SHORT).show();
            }else{
                RequestPhonePermission();
            }
            if(ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(LoginActivity.this, "You already granted permisssion.", Toast.LENGTH_SHORT).show();
            }else{
                RequestSendSMSPermission();
            }
            if(ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(LoginActivity.this, "You already granted permisssion.", Toast.LENGTH_SHORT).show();
            }else{
                RequestReceiveSMSPermission();
            }
            //MATCH VARIABLES
            btnLoginUser = findViewById(R.id.btnLogin);
            btnSignupUser = findViewById(R.id.btnSignup);
            txtEmailUser = findViewById(R.id.txtEmail);
            txtPasswordUser = findViewById(R.id.txtPassword);
            firebaseAuth = FirebaseAuth.getInstance();
            //VARIABLE PROGRAMMING
            btnLoginUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //LOGIN METHOD
                    userLogin(txtEmailUser.getText().toString(), txtPasswordUser.getText().toString());
                }
            });
            btnSignupUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openSignupdialog();
                }
            });
        }
    }

    private void RequestInternetPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed!")
                    .setMessage("This permission is needed because XYZ reasons.")
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions(LoginActivity.this, new String[] {Manifest.permission.INTERNET}, INTERNET_PERMISSION_CODE);
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            })
            .create().show();
        }else{
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.INTERNET}, INTERNET_PERMISSION_CODE);
        }
    }

    private void RequestLocationPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed!")
                    .setMessage("This permission is needed because XYZ reasons.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(LoginActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        }else{
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }

    private void RequestPhonePermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed!")
                    .setMessage("This permission is needed because XYZ reasons.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(LoginActivity.this, new String[] {Manifest.permission.CALL_PHONE}, PHONE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        }else{
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CALL_PHONE}, PHONE_PERMISSION_CODE);
        }
    }

    private void RequestSendSMSPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed!")
                    .setMessage("This permission is needed because XYZ reasons.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(LoginActivity.this, new String[] {Manifest.permission.SEND_SMS}, SENDSMS_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        }else{
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS}, SENDSMS_PERMISSION_CODE);
        }
    }

    private void RequestReceiveSMSPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed!")
                    .setMessage("This permission is needed because XYZ reasons.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(LoginActivity.this, new String[] {Manifest.permission.RECEIVE_SMS}, RECEIVESMS_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        }else{
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECEIVE_SMS}, RECEIVESMS_PERMISSION_CODE);
        }
    }

    private void openSignupdialog(){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private void userLogin(String emailUser, String passwordUser) {
        final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
        dialog.setMessage(getBaseContext().getResources().getString(R.string.Processing));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        firebaseAuth.signInWithEmailAndPassword(emailUser, passwordUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), R.string.loginSuccess, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, SOSActivity.class));
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), R.string.loginError, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
    }
}
