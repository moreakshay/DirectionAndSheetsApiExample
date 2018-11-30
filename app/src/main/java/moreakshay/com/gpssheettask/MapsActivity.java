package moreakshay.com.gpssheettask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.directions.route.Route;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import moreakshay.com.gpssheettask.helpers.GPSTracker;
import moreakshay.com.gpssheettask.helpers.RouteHelper;
import moreakshay.com.gpssheettask.helpers.SheetsHelper;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, RouteHelper.Listener
                                                                        ,SheetsHelper.Listener{

    private GoogleMap mMap;
    LatLng source, destination = new LatLng(19.112688, 72.861171);
    public static final int REQUEST_AUTHORIZATION = 1001;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS };
    SheetsHelper sheetsHelper;
    RouteHelper routeHelper;
    boolean isRouteFetched, isSheetFetched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
       init();
       sheetsHelper.getSheets();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getCurrentLocation();
        setMarkers();
        plotRoute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    sheetsHelper.getSheets();
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        routeHelper.unregisterListener(this);
    }

    private void init() {
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        credential.setSelectedAccountName(getSharedPreferences("myAccountName",Context.MODE_PRIVATE)
                .getString(PREF_ACCOUNT_NAME, null));
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        Sheets sheets = new Sheets
                .Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(getString(R.string.app_name)).build();
        sheetsHelper = new SheetsHelper(this, sheets);
        routeHelper = new RouteHelper();
        routeHelper.regiesterListener(this);
    }

    void plotRoute(){
        if (mMap != null && source != null) {
            mMap.addMarker(new MarkerOptions().position(source).title("your current location"));
            mMap.setMinZoomPreference(15);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(source));
            String url = getRequestUrl(source, destination);
            routeHelper.getRoute(url);
        }
    }

    private String getRequestUrl(LatLng origin, LatLng dest) {
        // API KEY
        String key = "key=AIzaSyCmFbhEs4f1FO5ipvLinxKbHzFupDpdlAs";
        //Value of origin
        String str_org = "origin=" + origin.latitude +","+origin.longitude;
        //Value of destination
        String str_dest = "destination=" + dest.latitude+","+dest.longitude;
        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode for find direction
        String mode = "mode=driving";
        //Build the full param
        String param = str_org +"%26" + str_dest + "%26" +sensor+"%26" +mode+"%26" +key;
        //Output format
        String output = "json";
        //Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        String serverUrl = "https://api.simbabeer.com/api/test/test/get_map_response?url=" + url;
        return serverUrl;
    }


    //Helper methods
    public void getCurrentLocation(){
        GPSTracker gpsTracker = new GPSTracker(this, this);
        source = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        Log.d("CURRENT LOCATION", source.latitude + " , " + source.longitude);
    }

    private void setMarkers(){
        if (source != null) {
            mMap.addMarker(new MarkerOptions().position(source).title("your current location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(source));
            mMap.setMinZoomPreference(17);
            mMap.addMarker(new MarkerOptions()
                    .position(destination)
                    .title("your destination")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }
    }

    @Override
    public void onRouteFetched(List<Route> routes) {
        isRouteFetched = true;
        for (Route route: routes) {
            mMap.addPolyline(routeHelper.getPolylineOptions(route));
        }
        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(source, 16);
        mMap.animateCamera(center);
        dumpData();
    }

    @Override
    public void sheetsFetched() {
        isSheetFetched = true;
        dumpData();
    }

    @Override
    public void sheetsRequestAuthorization(UserRecoverableAuthIOException e) {
        startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
    }

    @Override
    public void sheetFailed() {
        isSheetFetched = false;
    }

    private void dumpData(){
        if(isSheetFetched && isRouteFetched){
            Calendar calendar = Calendar.getInstance();
            Object time = new SimpleDateFormat("E, MMM d yyyy hh:mm a", Locale.getDefault()).format(calendar.getTime());
            Object objSource = source.latitude + "," + source.longitude;
            sheetsHelper.writeSheet(time, objSource, routeHelper.getDirections());
        }
    }
}
