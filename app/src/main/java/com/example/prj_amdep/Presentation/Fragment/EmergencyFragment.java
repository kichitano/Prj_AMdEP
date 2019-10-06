package com.example.prj_amdep.Presentation.Fragment;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.prj_amdep.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmergencyFragment extends Fragment implements View.OnClickListener, LocationListener {

    //VARIABLE DECLARATION
    ImageButton imgBtn1;
    ImageButton imgBtn2;
    ImageButton imgBtn3;
    ImageButton imgBtn4;

    int LOCATION_PERMISSION_CODE = 2;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private String latitude, longitude;

    public EmergencyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_emergency, container, false);
        //CHECK PERMISSION
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getActivity(), "You already granted permisssion.", Toast.LENGTH_SHORT).show();
        }else{
            RequestLocationPermission();
        }
        //START LOCATION
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5, this);

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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBtn1:
                new sendCoordinates().execute(latitude,longitude);
                break;
            case R.id.imgBtn2:
                break;
            case R.id.imgBtn3:
                break;
            case R.id.imgBtn4:
                break;
        }
    }

    private void RequestLocationPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)){
            new AlertDialog.Builder(getContext())
                    .setTitle("Permission needed!")
                    .setMessage("This permission is needed because XYZ reasons.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
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
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }

    private class sendCoordinates extends AsyncTask<String, Integer, String[]> {

        @Override
        protected String[] doInBackground(String... strings) {

        return strings;
        }

        protected void onPostExecute(String string[]){
            Toast.makeText(getContext(), latitude + " / " + longitude, Toast.LENGTH_SHORT).show();
        }
    }
}
