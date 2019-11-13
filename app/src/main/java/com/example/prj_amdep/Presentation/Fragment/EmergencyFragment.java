package com.example.prj_amdep.Presentation.Fragment;


import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.prj_amdep.Model.HelpRequestModel;
import com.example.prj_amdep.Model.StationModel;
import com.example.prj_amdep.Model.UserModel;
import com.example.prj_amdep.Presentation.SOSActivity;
import com.example.prj_amdep.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmergencyFragment extends Fragment implements View.OnClickListener, LocationListener {

    //VARIABLE DECLARATION
    private ImageButton imgBtn1;
    private ImageButton imgBtn2;
    private ImageButton imgBtn3;
    private ImageButton imgBtn4;
    private String option;
    private int indexStation;
    private LocationManager locationManager;
    public String latitude, longitude;
    private SOSActivity sosActivity;
    private DatabaseReference databaseReference;
    private DatabaseReference rootDatabaseReference;
    private DatabaseReference userDatabaseReference;
    private DataSnapshot dataSnapshot;
    private StationModel stationModel;
    private HelpRequestModel helpRequestModel;
    private PendingIntent pendingIntent;
    private SmsManager smsManager;
    private ArrayList<StationModel> stationModels = new ArrayList<>();
    final Map<String,Object> helpRequestMap = new HashMap<>();
    private Date requestDate = Calendar.getInstance().getTime();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private UserModel userModel;

    public EmergencyFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onAttach (Context context) {
        super.onAttach(context);
        //START LOCATION
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, this);
        //GET DATABASE REFERENCE
        sosActivity = (SOSActivity) getActivity();
        databaseReference = sosActivity.databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference("Stations");
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        rootDatabaseReference = sosActivity.databaseReference.getRoot();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_emergency, container, false);
        //PREPARE TO SEND SMS
        pendingIntent = PendingIntent.getActivity(getContext(),0, new Intent(getContext(), EmergencyFragment.class), 0);
        smsManager = SmsManager.getDefault();
        //PREPARE STRUCTURE FOR HELP REQUEST
        helpRequestModel = new HelpRequestModel();
        userModel = new UserModel();
        //MATCH VARIABLES
        imgBtn1 = view.findViewById(R.id.imgBtn1);
        imgBtn2 = view.findViewById(R.id.imgBtn2);
        imgBtn3 = view.findViewById(R.id.imgBtn3);
        imgBtn4 = view.findViewById(R.id.imgBtn4);
        //SET ONCLICK EVENTS
        imgBtn1.setOnClickListener(this);
        imgBtn2.setOnClickListener(this);
        imgBtn3.setOnClickListener(this);
        imgBtn4.setOnClickListener(this);
        //GET NEAREST STATIONS
        getUserData();
        getStations();
        return view;
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBtn1:
                option = "1";
                break;
            case R.id.imgBtn2:
                option = "2";
                break;
            case R.id.imgBtn3:
                option = "3";
                break;
            case R.id.imgBtn4:
                option = "4";
                break;
        }
        indexStation = nearestStation(Double.parseDouble(latitude),Double.parseDouble(longitude), option,stationModels);
        sendCoodinates();
        new callNumber().execute();
        Toast.makeText(getContext(), stationModels.get(indexStation).getStationName(), Toast.LENGTH_LONG).show();
    }

    private class callNumber extends AsyncTask<String, Integer, String[]> {
        @Override
        protected String[] doInBackground(String... strings) {
            //SEND MESSAGE
            smsManager.sendTextMessage(stationModels.get(indexStation).getStationMobile(), null, latitude + " | " + longitude, pendingIntent, null);
            return strings;
        }

        protected void onPostExecute(String string[]){
            //CALL NUMBER
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + stationModels.get(indexStation).getStationMobile()));
            startActivity(callIntent);
        }
    }

    private void sendCoodinates(){
        helpRequestModel.setUserDNI(userModel.getUserDNI());
        helpRequestModel.setStationID(stationModels.get(indexStation).getStationID());
        helpRequestModel.setRequestLatitude(latitude);
        helpRequestModel.setRequestLongitude(longitude);
        helpRequestModel.setRequestDate(dateFormat.format(requestDate.getTime()));
        helpRequestModel.setRequestTime(timeFormat.format(requestDate.getTime()));
        helpRequestModel.setRequestInformation("");
        helpRequestModel.setRequestTaken("");
        helpRequestModel.setRequestStatus("Pending");
        rootDatabaseReference.child("HelpRequest").push().setValue(helpRequestModel);
    }

    private void getStations(){
        //CLEAN ARRAYLIST
        stationModels.clear();
        //GET VALUES FROM SELECTED STATION
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    //SET A NEW STATION MODEL
                    stationModel = new StationModel();
                    stationModel.setStationID(postSnapshot.child("stationID").getValue().toString());
                    stationModel.setStationCategory(postSnapshot.child("stationCategory").getValue().toString());
                    stationModel.setStationLat(postSnapshot.child("stationLat").getValue().toString());
                    stationModel.setStationLon(postSnapshot.child("stationLon").getValue().toString());
                    stationModel.setStationMobile(postSnapshot.child("stationMobile").getValue().toString());
                    stationModel.setStationName(postSnapshot.child("stationName").getValue().toString());
                    stationModel.setStationPhone(postSnapshot.child("stationPhone").getValue().toString());
                    //ADD STATION MODEL TO OUR LIST
                    stationModels.add(stationModel);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getUserData(){
        //GET USER DATA
        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userModel.setUserDNI(dataSnapshot.child(sosActivity.firebaseAuth.getCurrentUser().getUid()).child("userDNI").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private static int nearestStation(double userLatitude, double userLongitude,String option, ArrayList<StationModel> stationModels){
        //SAVE DATA OF SELECTED STATION
        int index = 0;
        double lastResult = 0;
        //FILTERING STATIONS
        for (int i = 0; i < stationModels.size(); i++){
            //GET SELECTED OPTION
            if(stationModels.get(i).getStationCategory().equals(option)) {
                //CALCULATING DISTANCE IN METERS
                double earthRadius = 6371000; //METERS
                double dLat = Math.toRadians(Double.parseDouble(stationModels.get(i).getStationLat()) - userLatitude);
                double dLng = Math.toRadians(Double.parseDouble(stationModels.get(i).getStationLon()) - userLongitude);
                double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(Math.toRadians(userLatitude)) * Math.cos(Math.toRadians(Double.parseDouble(stationModels.get(i).getStationLat()))) *
                                Math.sin(dLng/2) * Math.sin(dLng/2);
                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
                double dist = earthRadius * c;
                //GET THE LOWEST DISTANCE BY INDEX ARRAY
                if( i == 0 ){
                    index = i;
                    lastResult = dist;
                }else{
                    if( dist < lastResult ){
                        index = i;
                        lastResult = dist;
                    }
                }
            }
        }
        return index;
    }
}
