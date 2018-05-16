package com.halaat.halaat.Activity;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.os.Handler;
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
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.barcode.Barcode;
import com.halaat.halaat.R;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Modal.LocationModel;
import Modal.tweetEnt;
import Views.ArrayListAdapter;
import Views.BinderSocialTweet;
import Views.ExpandedListView;
import helpers.AutoCompleteLocation;
import helpers.UIHelper;
import retrofit2.Call;

import static com.twitter.sdk.android.core.services.params.Geocode.Distance.KILOMETERS;

public class SocialTweetsListActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {


    private ExpandedListView lvRoadBlock;
    private ExpandedListView lvHeavyTraffic;
    private ExpandedListView lvBombBlast;
    private ExpandedListView lvSnatching;
    private ExpandedListView lvProtest;
    private Handler updateBarHandler;
    private List<tweetEnt> collection;
    private ArrayListAdapter<tweetEnt> adapterHeavyTraffic;
    private ArrayListAdapter<tweetEnt> adapterBombBlast;
    private ArrayListAdapter<tweetEnt> adapterSnatching;
    private ArrayListAdapter<tweetEnt> adapterProtest;
    private ArrayListAdapter<tweetEnt> adapterRoadBlock;
    private ArrayListAdapter<tweetEnt> adapter;

    AutoCompleteLocation autoComplete;
    private ProgressDialog pDialog;
    ImageView imgGps;
    GoogleApiClient mGoogleApiClient;
    private double locationLat;
    private double locationLng;
    private String userAddress;
    LatLng location;
    LatLng currLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_tweets_list);

        lvRoadBlock = (ExpandedListView) findViewById(R.id.lv_roadblock);
        lvBombBlast = (ExpandedListView) findViewById(R.id.lv_Bombblast);
        lvHeavyTraffic = (ExpandedListView) findViewById(R.id.lv_Heavytraffic);
        lvProtest = (ExpandedListView) findViewById(R.id.lv_Protest);
        lvSnatching = (ExpandedListView) findViewById(R.id.lv_Snatching);
        imgGps = (ImageView) findViewById(R.id.img_gps);
        updateBarHandler = new Handler();
        mGoogleApiClient = new GoogleApiClient
                .Builder(SocialTweetsListActivity.this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(SocialTweetsListActivity.this, 0, this)
                .build();

        autoComplete = (AutoCompleteLocation) findViewById(R.id.autocomplete);

        setLitner();
        setGpsIcon();

        new Thread(new Runnable() {

            @Override
            public void run() {
                loadTweets();
            }
        }).start();

    }

    private void loadRoadBlock() {

        Geocode userLoc = new Geocode(locationLat, locationLng, 25, KILOMETERS);
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        SearchService tweetSearchService = twitterApiClient.getSearchService();
        Call<Search> serviceCall = tweetSearchService.tweets("Road Block", userLoc, "en", null, "recent", 10, null, null, null, false);

        serviceCall.enqueue(new Callback<Search>() {
            @Override
            public void success(Result<Search> result) {

                //String[] listItems = new String[result.data.tweets.size()];
                ArrayList<tweetEnt> listItems = new ArrayList<>();
                int i = 0;
                if (result.response.isSuccessful()) {
                    List<Tweet> tweetsList = result.data.tweets;
                    for (Tweet t : tweetsList) {
                        listItems.add(new tweetEnt(t.text.toString(), t.createdAt, "10-30"));
                    }
                }

                adapterRoadBlock = new ArrayListAdapter<tweetEnt>(SocialTweetsListActivity.this, new BinderSocialTweet());
                adapterRoadBlock.clearList();
                lvRoadBlock.setAdapter(adapterRoadBlock);
                adapterRoadBlock.addAll(listItems);
                adapterRoadBlock.notifyDataSetChanged();

            }

            public void failure(TwitterException exception) {
                Log.d("Tweets: ", "failed");
            }
        });
    }

    private void loadBombBlastTraffic() {

        Geocode userLoc = new Geocode(locationLat, locationLng, 25, KILOMETERS);
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        SearchService tweetSearchService = twitterApiClient.getSearchService();
        Call<Search> serviceCall = tweetSearchService.tweets("Bombblast", userLoc, "en", null, "recent", 10, null, null, null, false);

        serviceCall.enqueue(new Callback<Search>() {
            @Override
            public void success(Result<Search> result) {

                //String[] listItems = new String[result.data.tweets.size()];
                ArrayList<tweetEnt> listItems = new ArrayList<>();
                int i = 0;
                if (result.response.isSuccessful()) {
                    List<Tweet> tweetsList = result.data.tweets;
                    for (Tweet t : tweetsList) {
                        listItems.add(new tweetEnt(t.text.toString(),  t.createdAt, "10-30"));
                    }
                }

                adapterBombBlast = new ArrayListAdapter<tweetEnt>(SocialTweetsListActivity.this, new BinderSocialTweet());
                adapterBombBlast.clearList();
                lvBombBlast.setAdapter(adapterBombBlast);
                adapterBombBlast.addAll(listItems);
                adapterBombBlast.notifyDataSetChanged();

            }

            public void failure(TwitterException exception) {
                Log.d("Tweets: ", "failed");
            }
        });
    }

    private void loadSnatchingTraffic() {

        Geocode userLoc = new Geocode(locationLat, locationLng, 25, KILOMETERS);
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        SearchService tweetSearchService = twitterApiClient.getSearchService();
        Call<Search> serviceCall = tweetSearchService.tweets("Snatching", userLoc, "en", null, "recent", 10, null, null, null, false);

        serviceCall.enqueue(new Callback<Search>() {
            @Override
            public void success(Result<Search> result) {

                //String[] listItems = new String[result.data.tweets.size()];
                ArrayList<tweetEnt> listItems = new ArrayList<>();
                int i = 0;
                if (result.response.isSuccessful()) {
                    List<Tweet> tweetsList = result.data.tweets;
                    for (Tweet t : tweetsList) {
                        listItems.add(new tweetEnt(t.text.toString(),  t.createdAt, "10-30"));
                    }
                }

                adapterSnatching = new ArrayListAdapter<tweetEnt>(SocialTweetsListActivity.this, new BinderSocialTweet());
                adapterSnatching.clearList();
                lvSnatching.setAdapter(adapterSnatching);
                adapterSnatching.addAll(listItems);
                adapterSnatching.notifyDataSetChanged();


            }

            public void failure(TwitterException exception) {
                Log.d("Tweets: ", "failed");
            }
        });
    }

    private void loadProtestTraffic() {

        Geocode userLoc = new Geocode(locationLat, locationLng, 25, KILOMETERS);
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        SearchService tweetSearchService = twitterApiClient.getSearchService();
        Call<Search> serviceCall = tweetSearchService.tweets("Protest", userLoc, "en", null, "recent", 10, null, null, null, false);

        serviceCall.enqueue(new Callback<Search>() {
            @Override
            public void success(Result<Search> result) {

                //String[] listItems = new String[result.data.tweets.size()];
                ArrayList<tweetEnt> listItems = new ArrayList<>();
                int i = 0;
                if (result.response.isSuccessful()) {
                    List<Tweet> tweetsList = result.data.tweets;
                    for (Tweet t : tweetsList) {
                        listItems.add(new tweetEnt(t.text.toString(),  t.createdAt, "10-30"));
                    }
                }

                adapterProtest = new ArrayListAdapter<tweetEnt>(SocialTweetsListActivity.this, new BinderSocialTweet());
                adapterProtest.clearList();
                lvProtest.setAdapter(adapterProtest);
                adapterProtest.addAll(listItems);
                adapterProtest.notifyDataSetChanged();

            }

            public void failure(TwitterException exception) {
                Log.d("Tweets: ", "failed");
            }
        });
    }

    private void loadHeavyTraffic() {

        Geocode userLoc = new Geocode(locationLat, locationLng, 25, KILOMETERS);
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        SearchService tweetSearchService = twitterApiClient.getSearchService();
        Call<Search> serviceCall = tweetSearchService.tweets("Traffic", userLoc, "en", null, "recent", 10, null, null, null, false);

        serviceCall.enqueue(new Callback<Search>() {
            @Override
            public void success(Result<Search> result) {

                //String[] listItems = new String[result.data.tweets.size()];
                ArrayList<tweetEnt> listItems = new ArrayList<>();
                int i = 0;
                if (result.response.isSuccessful()) {
                    List<Tweet> tweetsList = result.data.tweets;
                    for (Tweet t : tweetsList) {
                        listItems.add(new tweetEnt(t.text.toString(),  t.createdAt, "10-30"));
                    }
                }

                adapterHeavyTraffic = new ArrayListAdapter<tweetEnt>(SocialTweetsListActivity.this, new BinderSocialTweet());
                adapterHeavyTraffic.clearList();
                lvHeavyTraffic.setAdapter(adapterHeavyTraffic);
                adapterHeavyTraffic.addAll(listItems);
                adapterHeavyTraffic.notifyDataSetChanged();

            }

            public void failure(TwitterException exception) {
                Log.d("Tweets: ", "failed");
            }
        });
    }

    private void loadTweets() {

        loadRoadBlock();
        loadBombBlastTraffic();
        loadHeavyTraffic();
        loadProtestTraffic();
        loadSnatchingTraffic();
    }

    private void setLitner() {
        autoComplete.setAutoCompleteTextListener(new AutoCompleteLocation.AutoCompleteLocationListener() {
            @Override
            public void onTextClear() {
                locationLat = 0;
                locationLng = 0;
                loadTweets();
            }

            @Override
            public void onItemSelected(Place selectedPlace) {
                locationLat = selectedPlace.getLatLng().latitude;
                locationLng = selectedPlace.getLatLng().longitude;
                loadTweets();
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
            LocationModel locationModel = new LocationModel(userAddress, location.latitude, location.longitude);
            if (locationModel != null) {
                autoComplete.setText(locationModel.getAddress());
                locationLat = locationModel.getLat();
                locationLng = locationModel.getLng();
                loadTweets();
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

        locationLat = currLatLng.latitude;
        locationLng = currLatLng.longitude;
        getLocation(autoComplete);

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
