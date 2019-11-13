package com.example.prj_amdep.Presentation.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import com.example.prj_amdep.R;
import com.example.prj_amdep.Resources.DirectionsJSONParser;
import com.example.prj_amdep.Resources.MovableFloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PreventMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, View.OnClickListener {

    //VARIABLE DECLARATION
    private GoogleMap mMap;
    private MarkerOptions mCurrLocationMarker;
    private String location;
    private EditText locationSearch;
    private List<Address> addresses;
    private LatLng latLng, pLatLng;
    private MarkerOptions markerOptions;
    private Marker destinyMarker;
    private HashMap<String, Marker> hashMapMarker;
    private MovableFloatingActionButton fab;
    private CoordinatorLayout.LayoutParams lp;
    private Dialog dialogBuilder;
    private ImageButton drivingButton;
    private ImageButton walkingButton;
    private LinearLayout quicklyRoute;
    private String modeRoute;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    private static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);
    private Polyline mPolyline;

    @SuppressLint("MissingPermission")
    @Override
    public void onAttach (Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_prevent_map, container, false);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //INITIALIZE VARIABLES
        mCurrLocationMarker = new MarkerOptions();
        hashMapMarker = new HashMap<>();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //GET COMPONENTS OF VIEW
        locationSearch =  getActivity().findViewById(R.id.txtSearchDirections);
        locationSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    // Perform action on key press
                    location = String.valueOf(locationSearch.getText());
                    new GeocoderTask().execute(location);
                    return true;
                }
                return false;
            }
        });
        fab = getActivity().findViewById(R.id.fab);
        drivingButton = getActivity().findViewById(R.id.btnDriving);

        lp  = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        fab.setCoordinatorLayout(lp);
        //SETTING MENU IN FAB
        fab.setOnClickListener(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        // Check if we were successful in obtaining the map.
        if (mMap != null) {
            //OBTAIN POSITION
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            pLatLng = (new LatLng(location.getLatitude(),location.getLongitude()));
            //SET MARKER IN MAP
            mCurrLocationMarker.position(pLatLng);
            //SET TITLE OF MAP MARKER
            String mapTitle = String.valueOf(R.string.actualPosition);
            mCurrLocationMarker.title(mapTitle);
            //CUSTOM ICON MARKER MAP
            mCurrLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.personinmap));
            //ADDING MARKER INTO MAP
            mMap.addMarker(mCurrLocationMarker);
            //MOVE CAMERA TO POSITION AND ZOOM
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pLatLng,16));
        }
    }


    @Override
    public void onMapLongClick(LatLng point) {
        setMarkerLongPress(point);
    }

    private void showAlertDialog(int layout){
        dialogBuilder = new Dialog(getActivity());
        View layoutView = getLayoutInflater().inflate(layout, null);
        dialogBuilder.setContentView(layoutView);
        drivingButton = layoutView.findViewById(R.id.btnDriving);
        walkingButton = layoutView.findViewById(R.id.btnWalking);
        dialogBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBuilder.create();
        dialogBuilder.show();
        drivingButton.setOnClickListener(this);
        walkingButton.setOnClickListener(this);
    }

    private void showAlertDialog2(int layout){
        dialogBuilder = new Dialog(getActivity());
        View layoutView = getLayoutInflater().inflate(layout, null);
        dialogBuilder.setContentView(layoutView);
        quicklyRoute = layoutView.findViewById(R.id.btnQuicklyRoute);
        dialogBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBuilder.create();
        dialogBuilder.show();
        quicklyRoute.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                showAlertDialog(R.layout.fragment_prevent_map_mode_selector);
                break;
            case R.id.btnDriving:
                modeRoute = "driving";
                dialogBuilder.dismiss();
                showAlertDialog2(R.layout.fragment_prevent_map_option_selector);
                break;
            case R.id.btnWalking:
                modeRoute = "walking";
                dialogBuilder.dismiss();
                showAlertDialog2(R.layout.fragment_prevent_map_option_selector);
                break;
            case R.id.btnQuicklyRoute:
                dialogBuilder.dismiss();
                drawRoute();
                break;
        }
    }

    //*** An AsyncTask Background Process
    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            Geocoder geocoder = new Geocoder(getContext());
            addresses = null;
            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            if (addresses == null || addresses.size() == 0) {
                Toast.makeText(getContext(), String.valueOf(R.string.locationNotFound), Toast.LENGTH_SHORT).show();
            }//CLEAR LAST MAKER OF THE MAP AND ONLY SAVE FIRST
            if(!(hashMapMarker.isEmpty())){
                Marker marker = hashMapMarker.get("DESTINY");
                marker.remove();
                hashMapMarker.remove("DESTINY");
            }
            // Adding Markers on Google Map for each matching address
            for (int i = 0; i < addresses.size(); i++) {
                Address address = addresses.get(i);
                // Creating an instance of GeoPoint, to display in Google Map
                latLng = new LatLng(address.getLatitude(), address.getLongitude());
                String addressText = String.format("%s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getCountryName());
                markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(addressText);
                destinyMarker = mMap.addMarker(markerOptions);
                hashMapMarker.put("DESTINY",destinyMarker);
                // Locate the first location
                if (i == 0) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }
            }
        }
    }

    public void setMarkerLongPress(LatLng point){
        //CLEAR LAST MAKER OF THE MAP AND ONLY SAVE FIRST
        if(!(hashMapMarker.isEmpty())){
            Marker marker = hashMapMarker.get("DESTINY");
            marker.remove();
            hashMapMarker.remove("DESTINY");
        }
        // Adding Marker on Google Map for each matching address
        markerOptions = new MarkerOptions();
        markerOptions.position(point);
        markerOptions.title("Selected Address");
        destinyMarker = mMap.addMarker(markerOptions);
        hashMapMarker.put("DESTINY",destinyMarker);
        // Locate the first location
        mMap.animateCamera(CameraUpdateFactory.newLatLng(destinyMarker.getPosition()));
    }




    //TASKS TO DECODE POLYLINE

    private void drawRoute(){

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(pLatLng, hashMapMarker.get("DESTINY").getPosition());
        DownloadTask downloadTask = new DownloadTask();
        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }


    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
        // Key
        String key = "key=" + getString(R.string.google_maps_key);
        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+"mode="+modeRoute+"&"+"key=AIzaSyArp87t9K76YL5VUfuZJ97EkfBc6EA2PkQ";
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb  = new StringBuffer();
            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        }catch(Exception e){
            Log.d("Exception on download", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /** A class to download data from Google Directions URL */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("DownloadTask","DownloadTask : " + data);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Directions in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(6);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                if(mPolyline != null){
                    mPolyline.remove();
                }
                mPolyline = mMap.addPolyline(lineOptions);
                mPolyline.setPattern(PATTERN_POLYGON_ALPHA);
            }else
                Toast.makeText(getContext(),"No route is found", Toast.LENGTH_LONG).show();
        }
    }
}
