package moreakshay.com.gpssheettask;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback, RoutingListener, PermissionInjector {

    private GoogleMap mMap;
    LatLng source, destination = new LatLng(19.112688, 72.861171);
    private LocationManager mLocationManager;
    private long LOCATION_REFRESH_TIME = 1;
    private float LOCATION_REFRESH_DISTANCE = 1;
    private int LOCATION_PERMISSION = 990;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        getLocationPermission(this);


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
        plotRoute();
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);

    }

    @Override
    public void onRoutingFailure(RouteException e) {
        e.printStackTrace();
    }

    @Override
    public void onRoutingStart() {
        Toast.makeText(this, "routing started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int p) {
        Toast.makeText(this, "routing success", Toast.LENGTH_SHORT).show();
        CameraUpdate center = CameraUpdateFactory.newLatLng(source);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        mMap.moveCamera(center);


        List polylines = new ArrayList<>();
        //add route(s) to the mMap.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
//            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(Color.BLUE);
            polyOptions.width(5);
            List<LatLng> path = route.get(i).getPoints();
            polyOptions.addAll(path);
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }

        // Start marker
        /*MarkerOptions options = new MarkerOptions();
        options.position(source);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
        mMap.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(destination);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));
        mMap.addMarker(options);*/
    }

    @Override
    public void onRoutingCancelled() {
        Toast.makeText(this, "Routing Cancelled", Toast.LENGTH_SHORT).show();
    }


    void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            source = new LatLng(location.getLatitude(), location.getLongitude());
                            Log.d("CURRENT LOCATION", source.latitude + " , " + source.longitude);
                            if(mMap != null){
                                mMap.addMarker(new MarkerOptions().position(source).title("your current location"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(source));
                            }
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
                    });
        }

    }

    @Override
    public void permissionGranted() {
//        getCurrentLocation();
        GPSTracker gpsTracker = new GPSTracker(this, this);
        source = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        Log.d("CURRENT LOCATION", source.latitude + " , " + source.longitude);
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(source).title("your current location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(source));
        }
        plotRoute();
    }

    @Override
    public void permissionDenied() {

    }

    void plotRoute(){

        String key = "AIzaSyCmFbhEs4f1FO5ipvLinxKbHzFupDpdlAs";

        if (mMap != null && source != null) {
            mMap.addMarker(new MarkerOptions().position(source).title("your current location"));
            mMap.setMinZoomPreference(15);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(source));
            Routing routing = new Routing.Builder()
                    .travelMode(Routing.TravelMode.WALKING)
                    .withListener(this)
                    .waypoints(source, destination)
                    .key(key)
                    .build();
            routing.execute();
            mMap.addMarker(new MarkerOptions()
                    .position(destination)
                    .title("your destination")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }
    }

}
