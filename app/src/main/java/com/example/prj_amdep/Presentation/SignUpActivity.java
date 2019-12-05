package com.example.prj_amdep.Presentation;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.example.prj_amdep.Model.UserModel;
import com.example.prj_amdep.R;
import com.example.prj_amdep.Resources.AESCrypt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {


    //VARIABLES DECLARATION

    private EditText txtDNIuser;
    private EditText txtNameUser;
    private EditText txtLastnameUser;
    private EditText txtEmailUser;
    private EditText txtNicknameUser;
    private EditText txtPasswordUser;
    private EditText txtPhoneUser;

    private Button btnCheckDNIuser;
    private Button btnUserSignup;

    private HttpClient httpClient;
    private RequestQueue requestQueue;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //GET CORE NODE
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        //MATCH VARIABLES

        btnUserSignup = findViewById(R.id.btnSignupSubmit);
        btnCheckDNIuser = findViewById(R.id.btnCheckDNIuser);

        txtDNIuser = findViewById(R.id.txtDNIuser);
        txtNameUser = findViewById(R.id.txtNameUser);
        txtLastnameUser = findViewById(R.id.txtLastnameUser);
        txtEmailUser = findViewById(R.id.txtEmailUser);
        txtNicknameUser = findViewById(R.id.txtNicknameUser);
        txtPasswordUser = findViewById(R.id.txtPasswordUser);
        txtPhoneUser = findViewById(R.id.txtPhoneUser);

        //VARIABLE PROGRAMMING

        //CHECK DNI
        btnCheckDNIuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dniUser = txtDNIuser.getText().toString();
                try{
                    new postJSON().execute(dniUser);
                }catch (Exception e){
                    Log.i("AppTagError","Error in check DNI ... " + e.toString());
                }
            }
        });
        //SIGNUP USER
        btnUserSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //SET USER DATA TO MODEL
                    userModel = new UserModel();
                    userModel.setUserDNI(txtDNIuser.getText().toString());
                    userModel.setUserName(txtNameUser.getText().toString());
                    userModel.setUserLastname(txtLastnameUser.getText().toString());
                    userModel.setUserEmail(txtEmailUser.getText().toString());
                    userModel.setUserNickname(txtNicknameUser.getText().toString());
                    userModel.setUserPassword(txtPasswordUser.getText().toString());
                    userModel.setUserPhone(txtPhoneUser.getText().toString());
                    submitUser();
                }catch (Exception e){
                    Log.i("AppTagError","Error on submit ... " + e.toString());
                }
            }
        });
    }

    private class postJSON extends AsyncTask<String, Integer, String[]> {

        String[] userData = new String[2];
        @Override
        protected String[] doInBackground(String... strings) {
            try{
                JsonObject jsonObject = new JsonParser().parse(Submit(strings[0])).getAsJsonObject();
                if (jsonObject != null) {
                    userData[0] = jsonObject.get("nombres").getAsString();
                    String userLastName1 = jsonObject.get("paterno").getAsString();
                    String userLastName2 = jsonObject.get("materno").getAsString();
                    userData[1] = userLastName1 + " " + userLastName2;
                }
                return userData;
            }catch (Exception ex){
                Log.i("AppTagError","Some error ..." + ex.toString());
                return null;
            }
        }

        protected void onPostExecute(String[] userData){
            //Ches to see if the post was a success
            if(userData != null){
                txtNameUser.setText(userData[0]);
                txtLastnameUser.setText(userData[1]);
                Log.i("AppTag","(onPostExecute method) Result = Posted");
            }else{
                Log.i("AppTagError","(onPostExecute method) Result = Failed to post!");
            }
        }
    }

    private String Submit(String string) {
        String responseLine = null;
        StringBuilder response = null;
        try {
            URL url = new URL("http://www.dayangels.com/api/reniec/");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            String jsonInputString = "{\"user\":\"kixi.13.7@gmail.com\",\"pass\": \"cespedes\", \"dni\": " +string+'}';
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                        response = new StringBuilder();
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        System.out.println(response.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.toString();
    }

    public void submitUser() throws Exception {
        final ProgressDialog progDailog = new ProgressDialog(SignUpActivity.this);
        //SET MODEL AS HASHMAP
        final Map<String,Object> userMap = new HashMap<>();
        userMap.put("userDNI",userModel.getUserDNI());
        userMap.put("userName",userModel.getUserName());
        userMap.put("userLastname",userModel.getUserLastname());
        userMap.put("userEmail",userModel.getUserEmail());
        userMap.put("userNickname",userModel.getUserNickname());
        AESCrypt aesCrypt = new AESCrypt();
        String temp = aesCrypt.encryptPassword(userModel.getUserPassword());
        userMap.put("userPassword",temp);
        userMap.put("userPhone",userModel.getUserPhone());
        //SEND DATA TO DATABASE
        firebaseAuth.createUserWithEmailAndPassword(userModel.getUserEmail(),temp).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String idUser = firebaseAuth.getCurrentUser().getUid();
                    databaseReference.child("Users").child(idUser).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progDailog.dismiss();
                                Toast.makeText(getApplicationContext(), R.string.correctSubmit, Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                progDailog.dismiss();
                                Toast.makeText(getApplicationContext(), R.string.errorSubmit, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    progDailog.dismiss();
                    Toast.makeText(getApplicationContext(), R.string.errorSubmit, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
