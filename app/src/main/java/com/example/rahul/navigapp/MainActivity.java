package com.example.rahul.navigapp;

import android.*;
import android.Manifest;
import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;


import com.example.rahul.navigapp.POJO.Example;
import com.example.rahul.navigapp.POJO.Step;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sa90.materialarcmenu.ArcMenu;
import com.sa90.materialarcmenu.StateChangeListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,PlaceSelectionListener {


    private int zoom = -1;
    private int animateSpeed = -1;
    private boolean isAnimated = false;
    private double animateDistance = -1;
    private double animateCamera = -1;
    private int step = -1;
    private Polyline animateLine = null;
    private double totalAnimateDistance = 0;
    private boolean cameraLock = false;
    private OnAnimateListener mAnimateListener = null;
    private boolean flatMarker = false;
    private boolean isCameraTilt = false;
    private boolean isCameraZoom = false;
    private ArrayList<LatLng> animatePositionList = null;
    private boolean drawMarker = false;
    private boolean drawLine = false;

    private LatLng animateMarkerPosition = null;
    private LatLng beginPosition = null;
    private LatLng endPosition = null;
    private Marker animateMarker = null;
    Marker m;
    public final static int SPEED_VERY_FAST = 1;
    public final static int SPEED_FAST = 2;
    public final static int SPEED_NORMAL = 3;
    public final static int SPEED_SLOW = 4;
    public final static int SPEED_VERY_SLOW = 5;
    String searchString;






    private static final String LOG_TAG = "PlaceSelectionListener";
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    private static final int REQUEST_SELECT_PLACE = 1000;
    private TextView locationTextView;
    private TextView attributionsTextView;







    ///////////////////////////

    private GoogleMap mMap;
    LatLng mylocation;
    LatLng origin;
    LatLng dest;
    ArrayList<LatLng> MarkerPoints;
    TextView ShowDistanceDuration;
    Polyline line;
    TextToSpeech t1;
    LocationListener locationListener;
    GoogleApiClient mGoogleApiClient;
    ArrayList<LatLng> directionPoint;
    Marker marker;
    TextView tv;
    LatLng search_latLng;
    LocationRequest request;
    String instructions="";
    double latitude;
    double longitude;
    private int PROXIMITY_RADIUS = 10000;
    TextView dt;
    Marker mdefault;

    boolean navigate = true;
    ArcMenu arcMenuAndroid;
    FloatingActionButton floatingActionButton,floatingActionButton2,floatingActionButton3;
    String navigationtype = "driving";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dt = (TextView) findViewById(R.id.dandt);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        // Initializing
        MarkerPoints = new ArrayList<>();

        // Method #1
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);
        autocompleteFragment.setHint("Search a Location");
       // autocompleteFragment.setBoundsBias(BOUNDS_MOUNTAIN_VIEW);


        arcMenuAndroid = (ArcMenu) findViewById(R.id.arcmenu_android_example_layout);
        arcMenuAndroid.setStateChangeListener(new StateChangeListener() {
            @Override
            public void onMenuOpened() {
                //TODO something when menu is opened
            }
            @Override
            public void onMenuClosed() {
                //TODO something when menu is closed
            }
        });

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_arc_menu_1);
        floatingActionButton2 = (FloatingActionButton) findViewById(R.id.fab_arc_menu_2);
        floatingActionButton3 = (FloatingActionButton) findViewById(R.id.fab_arc_menu_3);



        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationtype = new String("bicycling");
                navigate();

            }
        });


        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationtype = new String("driving");
                navigate();

            }
        });
        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationtype = new String("walking");
                navigate();

            }
        });

        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //show error dialog if Google Play Services not available
        if (!isGooglePlayServicesAvailable()) {
            Toast.makeText(this,"Google Play Services not available",Toast.LENGTH_SHORT).show();
            Log.d("onCreate", "Google Play Services not available. Ending Test case.");
            finish();
        } else {
            Toast.makeText(this,"Google Play Services available",Toast.LENGTH_SHORT).show();
            Log.d("onCreate", "Google Play Services available. Continuing.");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }
    private void navigate(){
        navigate=true;
        if (MarkerPoints.size() == 2) {
                 Toast.makeText(getApplicationContext(), "Clear Direction Marker First!!!!", Toast.LENGTH_LONG).show();
        } else {
            if (MarkerPoints.size() == 1) {
                search_latLng = origin;

            }
            if(m != null)
                m.remove();

            if (MarkerPoints.size() == 2) {
                mMap.clear();
                MarkerPoints.clear();
                MarkerPoints = new ArrayList<>();
                //ShowDistanceDuration.setText("");
            }
            cameraLock = true;
            isCameraTilt = true;
            isCameraZoom = true;
            drawMarker = true;
            flatMarker = true;
            drawLine = true;

            if (MarkerPoints.size() == 1 && origin != null) {

                setLocationListenerOn();

            }
            else if(mylocation != null && marker != null){
                navigate = true;

            }
            else if (marker != null && search_latLng != origin && origin !=null) {

                setLocationListenerOn();
            }
            else if(mdefault != null ){
                setLocationListenerOn();
                            }

            else {
                     Toast.makeText(getApplicationContext(), "Search a valid location", Toast.LENGTH_LONG).show();
            }
        }


    }
    private void build_retrofit_and_get_directions(String type){
        String url = "https://maps.googleapis.com/maps/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitMaps service = retrofit.create(RetrofitMaps.class);

        Call<Example> call = service.getDistanceDuration("metric", origin.latitude + "," + origin.longitude,dest.latitude + "," + dest.longitude, type);
         call.enqueue(new Callback<Example>() {
             @Override
             public void onResponse(Response<Example> response, Retrofit retrofit) {
                 String status = response.body().getStatus();
                 if (status.equals("OK")) {
                     try {
                         //  Remove previous line from map
                         if (line != null) {
                             line.remove();
                         }
                         dt.setVisibility(View.VISIBLE);
                         // This loop will go through all the results and add marker on each location.
                         for (int i = 0; i < response.body().getRoutes().size(); i++) {
                             String distance = response.body().getRoutes().get(i).getLegs().get(i).getDistance().getText();
                             String time = response.body().getRoutes().get(i).getLegs().get(i).getDuration().getText();
                             dt.setText("Distance:" + distance+ " Distance:" + time );

                             String encodedString = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                             List<LatLng> list = decodePoly(encodedString);
                             line = mMap.addPolyline(new PolylineOptions()
                                     .addAll(list)
                                     .width(20)
                                     .color(Color.RED)
                                     .geodesic(true)
                             );
                         }
                         zoom(origin);

//                    animateDirection( directionPoint, SPEED_VERY_FAST
//               , true, false, true, true, new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.cars)), false, true, new PolylineOptions().width(3).color(Color.GREEN));


                     } catch (Exception e) {
                         Log.d("onResponse", "There is an error");
                         e.printStackTrace();
                     }


                 }
                 else{
                     Toast.makeText(getApplicationContext(), "No Route Found", Toast.LENGTH_LONG).show();
                     mMap.clear();
                 }
             }
             @Override
             public void onFailure(Throwable t) {

             }
         });


    }
    private void build_retrofit_and_get_response(String type ){

        String url = "https://maps.googleapis.com/maps/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitMaps service = retrofit.create(RetrofitMaps.class);
        Call<Example> call = service.getDistanceDuration("metric", mylocation.latitude + "," + mylocation.longitude,search_latLng.latitude + "," + search_latLng.longitude, navigationtype);
        // Call<Example> call = service.getDistance();
        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Response<Example> response, Retrofit retrofit) {
                ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
                String status = response.body().getStatus();
                if (status.equals("OK")) {
                    try {dt.setVisibility(View.VISIBLE);
                        //  Remove previous line from map
                        if (line != null) {
                            line.remove();
                        }
                        // This loop will go through all the results and add marker on each location.
                        for (int i = 0; i < response.body().getRoutes().size(); i++) {


                            String distance = response.body().getRoutes().get(i).getLegs().get(i).getDistance().getText();
                            String time = response.body().getRoutes().get(i).getLegs().get(i).getDuration().getText();

                            dt.setText("Distance:" + distance+ " Distance:" + time );


                            Step step = response.body().getRoutes().get(i).getLegs().get(i).getSteps().get(i);
                            String html_instructions = step.getHtml_instructions().replaceAll("<b>", "").replaceAll("</b>", "");

                            if (instructions.equals(response.body().getRoutes().get(i).getLegs().get(i).getSteps().get(i).getHtml_instructions())) {

                            } else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    t1.speak(html_instructions, TextToSpeech.QUEUE_FLUSH, null, null);
                                } else {
                                    t1.speak(html_instructions, TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }


                            //getting geopoints
                            double lat = Double.parseDouble(step.getStart_location().getLat());
                            double lng = Double.parseDouble(step.getStart_location().getLng());

                            listGeopoints.add(new LatLng(lat, lng));


                            ArrayList<LatLng> arr = (ArrayList<LatLng>) decodePoly(step.getPolyline().getPoints());
                            for (int j = 0; j < arr.size(); j++) {
                                listGeopoints.add(new LatLng(arr.get(j).latitude
                                        , arr.get(j).longitude));
                            }

                            double endlat = Double.parseDouble(step.getEnd_location().getLat());
                            double endlng = Double.parseDouble(step.getEnd_location().getLng());

                            //adding geopoints to arraylist

                            listGeopoints.add(new LatLng(endlat, endlng));

                            //encoding polylines

                            String encodedString = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                            List<LatLng> list = decodePoly(encodedString);
                            line = mMap.addPolyline(new PolylineOptions()
                                    .addAll(list)
                                    .width(20)
                                    .color(Color.RED)
                                    .geodesic(true)
                            );


                        }
                        instructions = new String(response.body().getRoutes().get(0).getLegs().get(0).getSteps().get(0).getHtml_instructions());


                        //zoom(mylocation);
                        directionPoint = listGeopoints;

                        if (directionPoint != null)
                            animateDirection(directionPoint, SPEED_VERY_FAST
                                    , cameraLock, isCameraTilt, isCameraZoom, drawMarker, new MarkerOptions().position(mylocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.carmarker)), flatMarker, drawLine, new PolylineOptions().width(3).color(Color.GREEN));


                    } catch (Exception e) {
                        Log.d("onResponse", "There is an error");
                        e.printStackTrace();
                    }

                    cameraLock = false;
                    isCameraTilt = false;
                    isCameraZoom = false;
                    drawMarker = true;
                    flatMarker = true;
                    drawLine = false;
                }
                else {Toast.makeText(getApplicationContext(),"No Route Found",Toast.LENGTH_LONG).show();}
                if(status.equals("ZERO_RESULTS")){
                    setLocationListenerOff();
                    mMap.clear();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("onFailure", t.toString());
            }
        });

    }

    //remove location listener
    private void setLocationListenerOff(){
        if(locationListener != null)
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,locationListener);

        instructions = new String("");
        if(animateMarker!=null)
            animateMarker.remove();
    }
    //set location listener
    private void setLocationListenerOn(){
        instructions = new String("");

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                LatLng Model_Town = new LatLng(location.getLatitude(), location.getLongitude());
                mylocation = Model_Town;

                latitude = location.getLatitude();
                longitude = location.getLongitude();

                if(mylocation != null && navigate == true)
                    build_retrofit_and_get_response("driving");
                //else Toast.makeText(getApplicationContext(), "Select Valid Location", Toast.LENGTH_SHORT).show();


            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, locationListener);


    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        navigate=false;


        Toast.makeText(getApplicationContext(), "Ready to Map!", Toast.LENGTH_SHORT).show();
         // request for navigation
        request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(5000);
        request.setFastestInterval(1000);

        setLocationListenerOn();

        }

    private void zoom(LatLng latlng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
//        animateDirection( directionPoint, SPEED_VERY_FAST
//                , true, false, true, true, new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.car)), false, true, new PolylineOptions().width(3).color(Color.GREEN));
//    }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        dt.setVisibility(View.GONE);

    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View view = getLayoutInflater().inflate(R.layout.sd, null);
            TextView textView = (TextView) view.findViewById(R.id.textView);
            textView.setText(marker.getTitle());
            TextView snipet = (TextView) view.findViewById(R.id.snippet);
            snipet.setText(marker.getSnippet());
            return view;
        }

    });

    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            search_latLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            mMap.clear();
            mdefault= mMap.addMarker(new MarkerOptions().position(search_latLng).title(marker.getTitle()).snippet(marker.getSnippet()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            navigate();
        }
    });


        // Add a marker in Sydney and move the camera
        LatLng Model_Town = new LatLng(28.7158727, 77.1910738);
      //  mMap.addMarker(new MarkerOptions().position(Model_Town).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Model_Town));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                   navigate=false;

                dt.setVisibility(View.GONE);

                setLocationListenerOff();

                if(mdefault != null)
                    mdefault.remove();

                if (search_latLng == origin) {
                    mMap.clear();
                    MarkerPoints.clear();
                    MarkerPoints = new ArrayList<>();
                    //ShowDistanceDuration.setText("");
                }
                if(m != null) {
                    m.remove();
                      }

                if (mylocation != null)
                    mylocation=null;

                if (marker != null)
                    marker.remove();

                if(origin!=null && dest!=null){
                    origin = null;
                    dest = null;
                }
                // clearing map and generating new marker points if user clicks on map more than two times
                if (MarkerPoints.size() > 1) {
                    mMap.clear();
                    MarkerPoints.clear();
                    MarkerPoints = new ArrayList<>();
                    //ShowDistanceDuration.setText("");
                }


                // Adding new item to the ArrayList
                MarkerPoints.add(point);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(point);

                /**
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED.
                 */
                if (MarkerPoints.size() == 1) {
                    Address position =  getTitle(MarkerPoints.get(0));
                       options.title(position.getAddressLine(0)).snippet(position.getCountryName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (MarkerPoints.size() == 2) {
                    Address position =  getTitle(MarkerPoints.get(0));
                    options.title(position.getAddressLine(0)).snippet(position.getCountryName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }


                // Add new marker to the Google Map Android API V2
                 mMap.addMarker(options);

                // Checks, whether start and end locations are captured

                if (MarkerPoints.size() == 1) {

                    origin = MarkerPoints.get(0);
                   // else Toast.makeText(getApplicationContext(),"Select valid locations",Toast.LENGTH_SHORT).show();
                }
                if (MarkerPoints.size() == 2) {
                    origin = MarkerPoints.get(0);
                    dest = MarkerPoints.get(1);
                }

                if(MarkerPoints.size() == 2) {
                    build_retrofit_and_get_directions("driving");
                }

                if(animateMarker != null)
                    animateMarker.remove();

                if (line != null)
                    line.remove();

            }
        });
    }

    private Address getTitle(LatLng latLng){
        List<Address> addresses = null;
        try {
            addresses = new Geocoder(MainActivity.this).getFromLocation(latLng.latitude,latLng.longitude,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address position = addresses.get(0);

        return position;
    }
//
//    private void hideSoftKeyboard(View v) {
//        InputMethodManager imm =
//                (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//    }
    public void geoLocate() throws IOException {
        navigate = false;
        dt.setVisibility(View.GONE);
        setLocationListenerOn();
        if(m != null){
            m.remove();
            mMap.clear();
        }
        if (line != null) {
            line.remove();
        }
        if(animateMarker != null)
            animateMarker.remove();
      //  hideSoftKeyboard(v);

        if(searchString.length()>0) {
           // Toast.makeText(this, "Searching for: " + searchString, Toast.LENGTH_SHORT).show();

            List<Address> addresses = new Geocoder(this).getFromLocationName(searchString, 1);
            Address position = addresses.get(0);
            search_latLng = new LatLng(position.getLatitude(), position.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(search_latLng, 5);
            mMap.moveCamera(cameraUpdate);
            String address = position.getAddressLine(0);
            String snippet = position.getCountryName();

            if (MarkerPoints.size() >= 1) {
                mMap.clear();
                MarkerPoints.clear();
                MarkerPoints = new ArrayList<>();
                //ShowDistanceDuration.setText("");
            }
            if (marker != null)
                marker.remove();
            marker = mMap.addMarker(new MarkerOptions().position(new LatLng(position.getLatitude(), position.getLongitude())).title(position.getLocality()).snippet(snippet + "\n" + address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }
        else Toast.makeText(this, "Enter Valid Location " + searchString, Toast.LENGTH_SHORT).show();
        }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        origin = null;
        dt.setVisibility(View.GONE);

        if (marker != null) {

            marker.remove();
            mMap.clear();
        }


        if (line != null)
            line.remove();

        if(origin!=null && dest!=null){
            origin = null;
            dest = null;
        }
        // clearing map and generating new marker points if user clicks on map more than two times
        if (MarkerPoints.size() >= 1) {
            mMap.clear();
            MarkerPoints.clear();
            MarkerPoints = new ArrayList<>();
            //ShowDistanceDuration.setText("");
        }
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if (id == R.id.nav_gallery) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else if (id == R.id.nav_slideshow) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        } else if (id == R.id.nav_manage) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        } else if (id == R.id.nav_share) {
            build_retrofit_and_get_nearbyplaces("restaurant");
        } else if (id == R.id.nav_send) {
            build_retrofit_and_get_nearbyplaces("hospital");
        }
        else if (id == R.id.nav_send2) {
            build_retrofit_and_get_nearbyplaces("school");

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Checking if Google Play Services Available or not
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }




    ///////////////////////////////////////animation coding



    public void animateDirection(ArrayList<LatLng> direction, int speed
            , boolean cameraLock1, boolean isCameraTilt1, boolean isCameraZoom1
            , boolean drawMarker1, MarkerOptions mo, boolean flatMarker1
            , boolean drawLine1, PolylineOptions po) {
        if(direction.size() > 1) {

            isAnimated = true;
            animatePositionList = direction;
            animateSpeed = speed;
            drawMarker = drawMarker1;
            drawLine = drawLine1;
            flatMarker = flatMarker1;
            isCameraTilt = isCameraTilt1;
            isCameraZoom = isCameraZoom1;
            step = 0;
            cameraLock = cameraLock1;

            setCameraUpdateSpeed(speed);

            beginPosition = direction.get(step);
            endPosition = direction.get(step + 1);
            animateMarkerPosition = beginPosition;

            if(mAnimateListener != null)
                mAnimateListener.onProgress(step, direction.size());

            if(cameraLock) {
                float bearing = getBearing(beginPosition, endPosition);
                CameraPosition.Builder cameraBuilder = new CameraPosition.Builder()
                        .target(animateMarkerPosition).bearing(bearing);

                if(isCameraTilt)
                    cameraBuilder.tilt(90);
                else
                    cameraBuilder.tilt(mMap.getCameraPosition().tilt);

                if(isCameraZoom)
                    cameraBuilder.zoom(zoom);
                else
                    cameraBuilder.zoom(mMap.getCameraPosition().zoom);

                CameraPosition cameraPosition = cameraBuilder.build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

            if(drawMarker) {
                if(animateMarker != null)
                    animateMarker.remove();
//\                animateMarker = mMap.addMarker(mo.position(beginPosition));

                //  animateMarker = mMap.addMarker(new MarkerOptions().position(beginPosition));
                animateMarker = mMap.addMarker(mo.position(beginPosition));

                if(flatMarker) {
                    animateMarker.setFlat(true);

                    float rotation = getBearing(animateMarkerPosition, endPosition) + 180;
                    animateMarker.setRotation(rotation);
                }
            }


            if(drawLine) {
                if(po != null)
                    animateLine = mMap.addPolyline(po.add(beginPosition)
                            .add(beginPosition).add(endPosition)
                            .width(dpToPx((int)po.getWidth())));
                else
                    animateLine = mMap.addPolyline(new PolylineOptions()
                            .width(dpToPx(5)));
            }

            new Handler().postDelayed(r, speed);
            if(mAnimateListener != null)
                mAnimateListener.onStart();
        }
    }

    private LatLng getNewPosition(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        double dis = Math.sqrt(Math.pow(lat, 2) + Math.pow(lng, 2));
        if(dis >= animateDistance) {
            double angle = -1;

            if(begin.latitude <= end.latitude && begin.longitude <= end.longitude)
                angle = Math.toDegrees(Math.atan(lng / lat));
            else if(begin.latitude > end.latitude && begin.longitude <= end.longitude)
                angle = (90 - Math.toDegrees(Math.atan(lng / lat))) + 90;
            else if(begin.latitude > end.latitude && begin.longitude > end.longitude)
                angle = Math.toDegrees(Math.atan(lng / lat)) + 180;
            else if(begin.latitude <= end.latitude && begin.longitude > end.longitude)
                angle = (90 - Math.toDegrees(Math.atan(lng / lat))) + 270;

            double x = Math.cos(Math.toRadians(angle)) * animateDistance;
            double y = Math.sin(Math.toRadians(angle)) * animateDistance;
            totalAnimateDistance += animateDistance;
            double finalLat = begin.latitude + x;
            double finalLng = begin.longitude + y;

            return new LatLng(finalLat, finalLng);
        } else {
            return end;
        }
    }

    private Runnable r = new Runnable() {
        public void run() {

            animateMarkerPosition = getNewPosition(animateMarkerPosition, endPosition);

            if(drawMarker)
                animateMarker.setPosition(animateMarkerPosition);


            if(drawLine) {
                List<LatLng> points = animateLine.getPoints();
                points.add(animateMarkerPosition);
                animateLine.setPoints(points);
            }

            if((animateMarkerPosition.latitude == endPosition.latitude
                    && animateMarkerPosition.longitude == endPosition.longitude)) {
                if(step == animatePositionList.size() - 2) {
                    isAnimated = false;
                    totalAnimateDistance = 0;
                    if(mAnimateListener != null)
                        mAnimateListener.onFinish();
                } else {
                    step++;
                    beginPosition = animatePositionList.get(step);
                    endPosition = animatePositionList.get(step + 1);
                    animateMarkerPosition = beginPosition;

                    if(flatMarker && step + 3 < animatePositionList.size() - 1) {
                        float rotation = getBearing(animateMarkerPosition, animatePositionList.get(step + 3)) + 180;
                        animateMarker.setRotation(rotation);
                    }

                    if(mAnimateListener != null)
                        mAnimateListener.onProgress(step, animatePositionList.size());
                }
            }

            if(cameraLock && (totalAnimateDistance > animateCamera || !isAnimated)) {
                totalAnimateDistance = 0;
                float bearing = getBearing(beginPosition, endPosition);
                CameraPosition.Builder cameraBuilder = new CameraPosition.Builder()
                        .target(animateMarkerPosition).bearing(bearing);

                if(isCameraTilt)
                    cameraBuilder.tilt(90);
                else
                    cameraBuilder.tilt(mMap.getCameraPosition().tilt);

                if(isCameraZoom)
                    cameraBuilder.zoom(zoom);
                else
                    cameraBuilder.zoom(mMap.getCameraPosition().zoom);

                CameraPosition cameraPosition = cameraBuilder.build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }

            if(isAnimated) {
                new Handler().postDelayed(r, animateSpeed);
            }
        }
    };



    public interface OnAnimateListener {
        public void onFinish();
        public void onStart();
        public void onProgress(int progress, int total);
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);
        if(begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float)(Math.toDegrees(Math.atan(lng / lat)));
        else if(begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float)((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if(begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return  (float)(Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if(begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float)((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    public void setCameraUpdateSpeed(int speed) {
        if(speed == SPEED_VERY_SLOW) {
            animateDistance = 0.000005;
            animateSpeed = 20;
            animateCamera = 0.0004;
            zoom = 19;
        } else if(speed == SPEED_SLOW) {
            animateDistance = 0.00001;
            animateSpeed = 20;
            animateCamera = 0.0008;
            zoom = 18;
        } else if(speed == SPEED_NORMAL) {
            animateDistance = 0.00005;
            animateSpeed = 20;
            animateCamera = 0.002;
            zoom = 16;
        } else if(speed == SPEED_FAST) {
            animateDistance = 0.0001;
            animateSpeed = 20;
            animateCamera = 0.004;
            zoom = 15;
        } else if(speed == SPEED_VERY_FAST) {
			/*animateDistance = 0.0005;
			animateSpeed = 20;
			animateCamera = 0.004;
			zoom = 13;
			*/
            animateDistance = 0.001;
            animateSpeed = 100;
            animateCamera = 0.04;
            zoom = 15;
        } else {
            animateDistance = 0.00005;
            animateSpeed = 20;
            animateCamera = 0.002;
            zoom = 16;
        }
    }


    private void build_retrofit_and_get_nearbyplaces(String type) {

        String url = "https://maps.googleapis.com/maps/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitMaps service = retrofit.create(RetrofitMaps.class);

        Call<Example> call = service.getNearbyPlaces(type, latitude + "," + longitude, PROXIMITY_RADIUS);

        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Response<Example> response, Retrofit retrofit) {

                try {
                    mMap.clear();
                    // This loop will go through all the results and add marker on each location.
                    for (int i = 0; i < response.body().getResults().size(); i++) {
                        Double lat = response.body().getResults().get(i).getGeometry().getLocation().getLat();
                        Double lng = response.body().getResults().get(i).getGeometry().getLocation().getLng();
                        String placeName = response.body().getResults().get(i).getName();
                        String vicinity = response.body().getResults().get(i).getVicinity();
                        MarkerOptions markerOptions = new MarkerOptions();
                        LatLng latLng = new LatLng(lat, lng);
                        // Position of Marker on Map
                        markerOptions.position(latLng);
                        // Adding Title to the Marker
                        markerOptions.title(placeName + " : " + vicinity);
                        // Adding Marker to the Camera.
                        m = mMap.addMarker(markerOptions);
                        // Adding colour to the marker
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        // move map camera
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                    }
                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }

                    setLocationListenerOff();

            }
            @Override
            public void onFailure(Throwable t) {
                Log.d("onFailure", t.toString());
            }
        });

    }




    @Override
    public void onPlaceSelected(Place place) {
        String searchplace = place.getName() +" "+place.getAddress();

        searchString = new String(searchplace);

        Log.i(LOG_TAG, "Place Selected: " + place.getName());
        Toast.makeText(this, "Searching For:" +searchplace ,
                Toast.LENGTH_LONG).show();

        try {
            geoLocate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Status status) {
        Log.e(LOG_TAG, "onError: Status = " + status.toString());
        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }


}
