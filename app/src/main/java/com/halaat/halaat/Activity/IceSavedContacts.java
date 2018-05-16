package com.halaat.halaat.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.halaat.halaat.Manifest;
import com.halaat.halaat.R;

import java.util.ArrayList;
import java.util.Map;

import Adapter.ContactAdapter;
import Adapter.ContactSavedAdapter;
import Modal.Contact;
import helpers.BasePreferenceHelper;

/**
 * Created by Hp on 3/7/2018.
 */

public class IceSavedContacts extends Activity {

    ListView listView;
    TextView iceContacts;
    TextView Contacts;
    TextView noData;
    Button sendNotification;
    private ArrayList<Contact> savedContacts = new ArrayList<>();
    BasePreferenceHelper prefHelper;
    private static final int PERMISSION_REQUEST_CODE = 1;
    String userId;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ice_saved_contacts);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        listView = (ListView) findViewById(R.id.lv_contacts);
        iceContacts = (TextView) findViewById(R.id.btn_ice_contacts);
        Contacts = (TextView) findViewById(R.id.btn_contacts);
        noData = (TextView) findViewById(R.id.no_data);
        sendNotification = (Button) findViewById(R.id.sendNotification);


        prefHelper = new BasePreferenceHelper(getApplicationContext());
/*
        Gson gson = new Gson();
        savedContacts = gson.fromJson(prefHelper.getSavedContacts(), new TypeToken<ArrayList<Contact>>() {}.getType());*/

        getContacts();


        setListners();
    }

    private void getContacts() {

        databaseReference.child("SelectedContact").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() == null) {
                            listView.setVisibility(View.GONE);
                            sendNotification.setVisibility(View.GONE);
                            noData.setVisibility(View.VISIBLE);

                        } else {
                            showContacts(((ArrayList<Map<String, Object>>) dataSnapshot.getValue()));

                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });


    }

    private void showContacts(ArrayList<Map<String, Object>> reports) {

        if (reports == null)
            return;
        savedContacts = new ArrayList<>();
        String key;
        String name = "";
        String phone = "";

        for (int i = 0; i < reports.size(); i++) {
            for (Map.Entry<String, Object> entry : reports.get(i).entrySet()) {

                //Get user map
                key = (String) entry.getKey();
                if (!key.equals("selected")) {
                    if (key.equals("phone")) {
                        phone = (String) entry.getValue();
                    } else if (key.equals("name")) {
                        name = (String) entry.getValue();
                        savedContacts.add(new Contact(name, phone));
                    }
                }
                //setData in savedContacts
            }
        }
        listView.setVisibility(View.VISIBLE);
        noData.setVisibility(View.GONE);
        sendNotification.setVisibility(View.VISIBLE);
          setListViewAdapter(savedContacts);
    }


    private void setListViewAdapter(ArrayList<Contact> savedContacts) {
        ContactSavedAdapter adapter = new ContactSavedAdapter(getApplicationContext(), savedContacts);
        listView.setAdapter(adapter);

    }

    private void setListners() {

        iceContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IceSavedContacts.this, IceSavedContacts.class);
                startActivity(intent);
            }
        });

        Contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(IceSavedContacts.this, ICE.class);
                startActivity(intent);


            }
        });
        sendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Contact item : savedContacts) {
                    sendSMS(item.getPhone(), "HALAAT TESTING");
                }
            }
        });

    }

    // ---------------- VOLUME TOP BUTTON TOPPED --------------------- //
    static int powerBtnTappedCount = 0;
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            powerBtnTappedCount ++;
            if(powerBtnTappedCount == 2){
                for (Contact item : savedContacts) {
                    sendSMS(item.getPhone(), "HALAAT TESTING");
                }
                powerBtnTappedCount = 0;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void sendSMS(String phoneNo, String msg) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

                if (checkSelfPermission(android.Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_DENIED) {

                    Log.d("permission", "permission denied to SEND_SMS - requesting it");
                    String[] permissions = {android.Manifest.permission.SEND_SMS};

                    requestPermissions(permissions, PERMISSION_REQUEST_CODE);

                }
            }
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}
