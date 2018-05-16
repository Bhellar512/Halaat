package com.halaat.halaat.Activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.halaat.halaat.R;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import Modal.LocationModel;
import Modal.Reports;
import helpers.AutoCompleteLocation;
import helpers.BasePreferenceHelper;
import helpers.UIHelper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddNewReportActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks {

    Spinner halaatTypesSpinner;
    TextView txtLocation;
    EditText comments;
    Button submitBtn;
    private double locationLat;
    private double locationLng;
    AutoCompleteLocation autoComplete;
    ImageView imgGps;
    private String address="";
    private String country="";
    private LocationManager locationManager;
    GoogleApiClient mGoogleApiClient;
    LatLng currLatLng;
    private String userAddress;
    LatLng location;


    DatabaseReference databaseReference;
    BasePreferenceHelper prefHelper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_report);

        imgGps=(ImageView)findViewById(R.id.img_gps);


        mGoogleApiClient = new GoogleApiClient
                .Builder(AddNewReportActivity.this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(AddNewReportActivity.this, 0, this)
                .build();

        autoComplete=(AutoCompleteLocation)findViewById(R.id.autocomplete) ;

        submitBtn = (Button) findViewById(R.id.sumbitBtn);
        submitBtn.setOnClickListener(this);

        comments = (EditText) findViewById(R.id.commentsEditText);

        prefHelper = new BasePreferenceHelper(getApplicationContext());
        databaseReference = FirebaseDatabase.getInstance().getReference();

        setLitner();
        setGpsIcon();

        setupHalaatTypesSpinner();
    }

    private void setLitner() {
        autoComplete.setAutoCompleteTextListener(new AutoCompleteLocation.AutoCompleteLocationListener() {
            @Override
            public void onTextClear() {

            }

            @Override
            public void onItemSelected(Place selectedPlace) {
                locationLat = selectedPlace.getLatLng().latitude;
                locationLng = selectedPlace.getLatLng().longitude;

            }
        });

        imgGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getLocation(autoComplete);
            }
        });
    }

    private void getLocation(AutoCompleteLocation autoComplete) {
        if (statusCheck()) {
            LocationModel locationModel = new LocationModel(userAddress,location.latitude,location.longitude);
            if (locationModel != null) {
                autoComplete.setText(locationModel.getAddress());
                locationLat = locationModel.getLat();
                locationLng = locationModel.getLng();
            } else {
                getLocation(autoComplete);
            }
        }
    }

    private void setGpsIcon() {

        autoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().equals("")) {
                    imgGps.setVisibility(View.VISIBLE);
                } else {
                    imgGps.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setupHalaatTypesSpinner() {
        halaatTypesSpinner = (Spinner) findViewById(R.id.halaatTypesSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.halaatTypes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        halaatTypesSpinner.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.sumbitBtn){
            String halaatType = halaatTypesSpinner.getItemAtPosition(halaatTypesSpinner.getSelectedItemPosition()).toString();
            String userComments = comments.getText().toString();
            String userLocation = autoComplete.getText().toString();
            String userId = prefHelper.getUserId();
            Reports modal = new Reports(userLocation, userId, userComments, halaatType,locationLat,locationLng);
            databaseReference.child("Reports").push().setValue(modal);

           DatabaseReference drUser = FirebaseDatabase.getInstance().getReference().child("Users") ;           // Send firebase push notification here
            String city=drUser.child("city").toString();
            if(city=="karachi") {
                sendNotification("Halaat update", "There's a new update regarding " + halaatType);
            }

            Intent intent=new Intent(AddNewReportActivity.this,LocationActivity.class);
            startActivity(intent);
        }
    }

    private void sendNotification (final String title, final String detail) {
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json=new JSONObject();
                    JSONObject dataJson=new JSONObject();
                    dataJson.put("body",detail);
                    dataJson.put("title",title);
                    json.put("notification",dataJson);
                    json.put("to","/topics/all");
                    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization","key="+"AIzaSyANIa-S3GKAfts8dvmGio3c0hiCqjS4MkA")
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                }catch (Exception e){
                }
                return null;
            }
        }.execute();
    }

    public boolean statusCheck() {
        if (isNetworkAvailable()) {
            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();
                return false;
            } else {
                return true;
            }
        } else {
            UIHelper.showShortToastInCenter(this, getString(R.string.internet_not_connected));
            return false;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(getString(R.string.gps_question))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.gps_yes), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(getResources().getString(R.string.gps_no), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
        location = new LatLng(currLatLng.latitude, currLatLng.longitude);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location, 14.5f);
        // mMap.addMarker(new MarkerOptions().position(location).title("my current location"));

        userAddress = getCompleteAddressString(currLatLng.latitude, currLatLng.longitude);


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
}

