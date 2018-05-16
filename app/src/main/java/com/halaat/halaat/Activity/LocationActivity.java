package com.halaat.halaat.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.halaat.halaat.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.core.services.params.Geocode;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import Modal.AreaLatLngEnt;
import Modal.TweetsAreaEnt;
import Modal.tweetEnt;
import Modules.DirectionFinder;
import Modules.DirectionFinderListener;
import Modules.Route;
import Views.ArrayListAdapter;
import Views.BinderSocialTweet;
import helpers.DialogHelper;
import retrofit2.Call;

import static com.twitter.sdk.android.core.services.params.Geocode.Distance.KILOMETERS;


public class LocationActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    LatLng currLatLng;
    EditText txtSource;
    EditText txtDestination;
    Button btnViewTweets;
    Button btnNewHalaat;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private String userAddress;
    Marker MyMarker;
    String dialogeTitle = "";
    String dialogeDetail = "";
    ArrayList<TweetsAreaEnt> areaArray = new ArrayList<>();
    ArrayList<AreaLatLngEnt> areaLatLngArray = new ArrayList<>();


    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        // btnChngAddr = (Button) findViewById(R.id.changeAddress);
        btnViewTweets = (Button) findViewById(R.id.viewTweets);
        btnNewHalaat = (Button) findViewById(R.id.reportNewHalaat);

        mGoogleApiClient = new GoogleApiClient
                .Builder(LocationActivity.this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(LocationActivity.this, 0, this)
                .build();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        databaseReference = FirebaseDatabase.getInstance().getReference();


        setupButtonActions();
        loadUserHalaatUpdates();


    }

    private void halatUpdateMarkers(double latitude, double longitude) {
        Geocode userLoc = new Geocode(latitude, longitude, 25, KILOMETERS);
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        SearchService tweetSearchService = twitterApiClient.getSearchService();
        Call<Search> serviceCall = tweetSearchService.tweets("Traffic+OR+Snatching+OR+Bombblast+OR+Protest", userLoc, "en", null, null, 10, null, null, null, false);

        serviceCall.enqueue(new Callback<Search>() {
            @Override
            public void success(Result<Search> result) {

                //String[] listItems = new String[result.data.tweets.size()];
                ArrayList<tweetEnt> listItems = new ArrayList<>();
                int i = 0;
                if (result.response.isSuccessful()) {
                    List<Tweet> tweetsList = result.data.tweets;
                    for (Tweet t : tweetsList) {
                        // if (Pattern.compile(Pattern.quote(t.text.toString()), Pattern.CASE_INSENSITIVE).matcher("jail").find()) {
                        if (t.text.toLowerCase().contains("jail chowrangi".toLowerCase())) {
                            areaArray.add(new TweetsAreaEnt("jail chowrangi", t.text.toString()));
                        } else if (t.text.toLowerCase().contains("Alladin Park".toLowerCase())) {
                            areaArray.add(new TweetsAreaEnt("Alladin Park", t.text.toString()));

                        }

                    }
                    getAreaDetail(areaArray);
                }

            }

            public void failure(TwitterException exception) {
                Log.d("Tweets: ", "failed");
            }
        });
    }

    private void getAreaDetail(ArrayList<TweetsAreaEnt> areaArray) {

        for (TweetsAreaEnt item : areaArray) {
            Random rand = new Random();
            int random = rand.nextInt(5000) + 1;
            areaLatLngArray.add(new AreaLatLngEnt(getLocationFromAddress(getApplicationContext(), item.getAreaName()), item.getTweetDetail(), item.getAreaName(), random + ""));
        }

        for (AreaLatLngEnt item : areaLatLngArray) {
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.tweet_marker);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap markerIcon = Bitmap.createScaledBitmap(b, 110, 130, false);


            LatLng loc = new LatLng(item.getLatLng().latitude, item.getLatLng().longitude);
            // MyMarker=mMap.addMarker(new MarkerOptions().position(loc).title(type).icon(BitmapDescriptorFactory.fromBitmap(markerIcon)).flat(true));
            MyMarker = mMap.addMarker(new MarkerOptions().position(loc).icon(BitmapDescriptorFactory.fromBitmap(markerIcon)));
            MyMarker.setTag(item.getRandom());

        }


    }


    private int getIconForHalaatType(String type) {
        switch (type) {
            case "Roadblock":
                return R.drawable.roadblock;
            case "Heavytraffic":
                return R.drawable.trafic;
            case "Protest":
                return R.drawable.protest;
            case "Snatching":
                return R.drawable.snatching;
            case "Bombblast":
                return R.drawable.bomb_blast;
            default:
                return 0;
        }
    }

    private void setupButtonActions() {
        btnViewTweets.setOnClickListener(this);
        //btnChngAddr.setOnClickListener(this);
        btnNewHalaat.setOnClickListener(this);
    }

    private void collectReports(Map<String, Object> reports) {

        if (reports == null)
            return;

        //iterate through each reports
        for (Map.Entry<String, Object> entry : reports.entrySet()) {

            //Get user map
            Map report = (Map) entry.getValue();
            String lat = report.get("latitude").toString();
            String longt = report.get("longitude").toString();
            String type = report.get("reportType").toString();

            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(getIconForHalaatType(type));
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap markerIcon = Bitmap.createScaledBitmap(b, 110, 130, false);


            LatLng loc = new LatLng(Double.parseDouble(lat), Double.parseDouble(longt));
            // MyMarker=mMap.addMarker(new MarkerOptions().position(loc).title(type).icon(BitmapDescriptorFactory.fromBitmap(markerIcon)).flat(true));
            MyMarker = mMap.addMarker(new MarkerOptions().position(loc).icon(BitmapDescriptorFactory.fromBitmap(markerIcon)));
            MyMarker.setTag(entry.getKey());

        }

    }


    private void loadUserHalaatUpdates() {
        // load aa submitted user reports

        ArrayList<String> ReportsList;

        databaseReference.child("Reports").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        collectReports((Map<String, Object>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    private void reportNewHalaat() {
    }

    private void changeAddress() {
    }

    private void viewTweets() {

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

        mMap.setOnMarkerClickListener(this);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        mMap.setMyLocationEnabled(true);


        // Add a marker in Sydney and move the camera

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location locationManager = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        currLatLng = new LatLng(locationManager.getLatitude(), locationManager.getLongitude());

        if (currLatLng != null)
            halatUpdateMarkers(currLatLng.latitude, currLatLng.longitude);

        LatLng location = new LatLng(currLatLng.latitude, currLatLng.longitude);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location, 14.5f);
        // mMap.addMarker(new MarkerOptions().position(location).title("my current location"));
        mMap.moveCamera(update);

        userAddress = getCompleteAddressString(currLatLng.latitude, currLatLng.longitude);

    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return strAdd;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.viewTweets) {
            Log.d("", "Tweets btn tapped");
            Intent intent = new Intent(LocationActivity.this, SocialTweetsListActivity.class);
            startActivity(intent);

        } else if (view.getId() == R.id.reportNewHalaat) {
            Log.d("", "Report enw halaat btn tapped");
            Intent intent = new Intent(LocationActivity.this, AddNewReportActivity.class);
            intent.putExtra("userLoc", userAddress);
            intent.putExtra("lat", currLatLng.latitude);
            intent.putExtra("long", currLatLng.longitude);
            startActivity(intent);
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        if (areaLatLngArray.size() > 0) {
            for (AreaLatLngEnt item : areaLatLngArray) {
                if (marker.getTag().equals(item.getRandom())) {
                    DialogHelper dialogHelper = new DialogHelper(LocationActivity.this);
                    dialogHelper.mapDialoge(R.layout.map_dialoge, item.getAreaName(), item.getTweetDetail());
                    dialogHelper.showDialog();
                }
            }
        }


        databaseReference.child("Reports").child(String.valueOf(marker.getTag())).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        if (dataSnapshot!=null && dataSnapshot.getValue()!=null && dataSnapshot.child("reportType").getValue().toString() != null && dataSnapshot.child("userComments").getValue().toString() != null) {
                            dialogeTitle = dataSnapshot.child("reportType").getValue().toString();
                            dialogeDetail = dataSnapshot.child("userComments").getValue().toString();


                            DialogHelper dialogHelper = new DialogHelper(LocationActivity.this);
                            dialogHelper.mapDialoge(R.layout.map_dialoge, dialogeTitle, dialogeDetail);
                            dialogHelper.showDialog();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });


        return false;
    }
}
