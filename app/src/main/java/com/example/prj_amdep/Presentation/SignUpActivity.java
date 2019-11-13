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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prj_amdep.Model.UserModel;
import com.example.prj_amdep.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
                    HttpParams httpParams = new BasicHttpParams();
                    //Set timeout if internet connection is very slow
                    int timeoutSocket = 20000;
                    int timeoutConnection = 20000;
                    HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
                    HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);
                    httpClient = new DefaultHttpClient(httpParams);
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

            private String url = "https://consulta.pe/api/reniec/dni";
            String[] userData = new String[2];

            @Override
            protected String[] doInBackground(String... strings) {
                try{
                    //Set variables
                    HttpPost httpPost = new HttpPost(url);
                    List<NameValuePair> nameValuePair = new ArrayList<>();
                    //Holds values that will get sent to the rest service
                    nameValuePair.add(new BasicNameValuePair("dni",strings[0]));
                    //Set the holders to json type so their server knows how to handle the incoming http request
                    httpPost.addHeader("Accept","application/json");
                    httpPost.addHeader("Authorization", "Bearer QBGnEtu9epRNSCQGlA6HNCiAWLZeOwXwOxjjMOWL");
                    //Set the payload with the named pair set as above
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, HTTP.UTF_8));
                    //Execute the post
                    HttpResponse response = httpClient.execute(httpPost);
                    //Return values
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        String retSrc = EntityUtils.toString(entity);
                        // parsing JSON
                        JSONObject result = new JSONObject(retSrc); //Convert String to JSON Object
                        userData[0] = result.getString("nombres");
                        String userLastName1 = result.getString("apellido_paterno");
                        String userLastName2 = result.getString("apellido_materno");
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

        public void submitUser(){
            final ProgressDialog progDailog = new ProgressDialog(SignUpActivity.this);
            //SET MODEL AS HASHMAP
            final Map<String,Object> userMap = new HashMap<>();
            userMap.put("userDNI",userModel.getUserDNI());
            userMap.put("userName",userModel.getUserName());
            userMap.put("userLastname",userModel.getUserLastname());
            userMap.put("userEmail",userModel.getUserEmail());
            userMap.put("userNickname",userModel.getUserNickname());
            userMap.put("userPassword",userModel.getUserPassword());
            userMap.put("userPhone",userModel.getUserPhone());
            //SEND DATA TO DATABASE
            firebaseAuth.createUserWithEmailAndPassword(userModel.getUserEmail(),userModel.getUserPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
