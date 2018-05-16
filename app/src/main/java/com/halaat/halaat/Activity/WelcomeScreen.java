package com.halaat.halaat.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.halaat.halaat.R;

import helpers.BasePreferenceHelper;

public class WelcomeScreen extends AppCompatActivity implements View.OnClickListener {
    DatabaseReference databaseReference;
    BasePreferenceHelper preferenceHelper;
    Button halaat, ICE, fixit, audio, logout;
    TextView nameView, phoneView, addressView;
    LinearLayout mainFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        mainFrame=(LinearLayout)findViewById(R.id.mainFrame) ;
        mainFrame.setVisibility(View.GONE);
        nameView = (TextView) findViewById(R.id.txt_username);
        phoneView = (TextView) findViewById(R.id.txt_phone);
        addressView = (TextView) findViewById(R.id.address);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        preferenceHelper = new BasePreferenceHelper(getApplicationContext());
        linkXML();
        setFonts();
        setListener();
        work();
        FirebaseMessaging.getInstance().subscribeToTopic("all");

    }

    private void setFonts() {



        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        firebaseUser.getUid();


        databaseReference.child("Users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        nameView.setText(dataSnapshot.child("name").getValue().toString());
                        phoneView.setText(dataSnapshot.child("mobile").getValue().toString());
                        addressView.setText(dataSnapshot.child("city").getValue().toString());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

        mainFrame.setVisibility(View.VISIBLE);







    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void linkXML() {
        halaat = findViewById(R.id.halaat);
        ICE = findViewById(R.id.ice_button);
        ICE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(WelcomeScreen.this, IceSavedContacts.class);
                startActivity(intent);
            }
        });
        fixit = findViewById(R.id.fixit_photo);
        audio = findViewById(R.id.audio);
        logout = findViewById(R.id.btn_logout);
    }

    public void setListener() {
        halaat.setOnClickListener(this);
        fixit.setOnClickListener(this);
        audio.setOnClickListener(this);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferenceHelper.setLoginStatus(false);
                Intent intent = new Intent(WelcomeScreen.this, SignIn.class);
                startActivity(intent);
               finish();
            }
        });
    }

    public void work() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fixit_photo) {
            Intent intent = new Intent(WelcomeScreen.this, FixitPhoto.class);
            startActivity(intent);
        }

        if (view.getId() == R.id.halaat) {
            Intent intent = new Intent(WelcomeScreen.this, LocationActivity.class);
            startActivity(intent);
        }
        if (view.getId()==R.id.audio)
        {
            Intent intent = new Intent (WelcomeScreen.this,RecordAudioActivity.class);
            startActivity(intent);

        }

    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        WelcomeScreen.super.onBackPressed();
                    }
                }).create().show();
    }
}
