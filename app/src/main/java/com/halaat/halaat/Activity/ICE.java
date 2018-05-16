package com.halaat.halaat.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
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
import com.halaat.halaat.R;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import Adapter.ContactAdapter;
import Modal.Contact;
import Modal.ContactNewModel;
import helpers.BasePreferenceHelper;
import helpers.InternetHelper;


public class ICE extends Activity {
    private ListView mListView;
    private ProgressDialog pDialog;
    private Handler updateBarHandler;
    private Button btnSave;
    private ArrayList<Contact> savedContacts;
    private BasePreferenceHelper prefHelper;
    TextView iceContacts;
    TextView ContactsBtn;
    EditText searchBox;
    TextView noData;
    DatabaseReference databaseReference;
    String userId;
    public static final int READ_CONTACT = 0;


    private static final String PARAM_REQUEST_IN_PROCESS = "requestPermissionsInProcess";

    private static final int REQUEST_PERMISSION = 3;
    private static final String PREFERENCE_PERMISSION_DENIED = "PREFERENCE_PERMISSION_DENIED";

    private AtomicBoolean mRequestPermissionsInProcess = new AtomicBoolean();

    ArrayList<Contact> contactList;
    Cursor cursor;
    int counter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ice);

        iceContacts = (TextView) findViewById(R.id.btn_ice_contacts);
        ContactsBtn = (TextView) findViewById(R.id.btn_contacts);
        searchBox = (EditText) findViewById(R.id.searchBox);
        noData = (TextView) findViewById(R.id.no_data);

        databaseReference = FirebaseDatabase.getInstance().getReference();


        prefHelper = new BasePreferenceHelper(getApplicationContext());
        checkPermissions(new String[]{android.Manifest.permission.READ_CONTACTS});


        mListView = (ListView) findViewById(R.id.list);
        btnSave = (Button) findViewById(R.id.btn_save);

        setListners();
        updateBarHandler = new Handler();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();

        if (InternetHelper.CheckInternetConectivityandShowToast(ICE.this)) {
            checkIsContacts();
        }
       /* pDialog = new ProgressDialog(ICE.this);
        pDialog.setMessage("Reading contacts...");
        pDialog.setCancelable(false);
        pDialog.show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                getContacts();
            }
        }).start();*/

        // Since reading contacts takes more time, let's run it on a separate thread.


        // Set onclicklistener to the list item.
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //TODO Do whatever you want with the list data
                Toast.makeText(getApplicationContext(), "item clicked : \n" + contactList.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIsContacts() {


        databaseReference.child("contact").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() == null) {
                            pDialog = new ProgressDialog(ICE.this);
                            pDialog.setMessage("Reading contacts...");
                            pDialog.setCancelable(false);
                            pDialog.show();
                            new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    getContacts();
                                }
                            }).start();
                        } else {
                            collectReports(((ArrayList<Map<String,Object>>) dataSnapshot.getValue()));

                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    private void collectReports(ArrayList<Map<String, Object>> reports) {

        if (reports == null)
            return;
        contactList = new ArrayList<>();

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
                        contactList.add(new Contact(name, phone));
                    }
                }
                //setData in savedContacts
            }
        }

        ContactAdapter adapter = new ContactAdapter(getApplicationContext(), R.layout.contact_item, contactList, prefHelper);
        mListView.setAdapter(adapter);


    }


    private void setListners() {

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                savedContacts = new ArrayList<>();
                for (Contact item : contactList) {
                    if (item.isSelected()) {
                        savedContacts.add(item);
                        Toast.makeText(getApplicationContext(), "Contact Saved",
                                Toast.LENGTH_LONG).show();

                    }

                }
                databaseReference.child("SelectedContact").child(userId).setValue(savedContacts);
                Gson gson = new Gson();
// This can be any object. Does not have to be an arraylist.
                String savedCon = gson.toJson(savedContacts);
                prefHelper.setSavedContacts(savedCon);
                Intent intent = new Intent(ICE.this, IceSavedContacts.class);
                startActivity(intent);
            }
        });

        iceContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ICE.this, IceSavedContacts.class);
                startActivity(intent);
            }
        });

        ContactsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ICE.this, ICE.class);
                startActivity(intent);


            }
        });

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                bindData(getSearchedArray(s.toString()));
            }
        });
    }



    public ArrayList<Contact> getSearchedArray(String keyword) {
        if (contactList.isEmpty()) {
            return new ArrayList<>();
        }

        ArrayList<Contact> arrayList = new ArrayList<>();

        for (Contact item : contactList) {
            String UserName = "";
            if (item.getName() != null) {
                UserName = item.getName();
            }
            if (Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE).matcher(UserName).find()) {
                /*UserName.contains(keyword)*/
                arrayList.add(item);
            }
        }
        return arrayList;

    }

    private void bindData(ArrayList<Contact> userCollection) {

        if (userCollection.size() <= 0) {
            noData.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else {
            noData.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }

        ContactAdapter adapter = new ContactAdapter(getApplicationContext(), R.layout.contact_item, userCollection, prefHelper);
        mListView.setAdapter(adapter);

       /* adapter.addAll(userCollection);
        adapter.notifyDataSetChanged();*/
    }

    private void checkPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissionInternal(permissions);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkPermissionInternal(String[] permissions) {
        ArrayList<String> requestPerms = new ArrayList<String>();
        for (String permission : permissions) {
            if (checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED && !userDeniedPermissionAfterRationale(permission)) {
                requestPerms.add(permission);
            }
        }
        if (requestPerms.size() > 0 && !mRequestPermissionsInProcess.getAndSet(true)) {
            //  We do not have this essential permission, ask for it
            requestPermissions(requestPerms.toArray(new String[requestPerms.size()]), REQUEST_PERMISSION);
            return true;
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    if (android.Manifest.permission.READ_CONTACTS.equals(permission)) {
                        showRationale(permission, R.string.permission_denied_contacts);
                    }
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showRationale(final String permission, int promptResId) {
        if (shouldShowRequestPermissionRationale(permission) && !userDeniedPermissionAfterRationale(permission)) {

            //  Notify the user of the reduction in functionality and possibly exit (app dependent)
            new AlertDialog.Builder(this)
                    .setTitle(R.string.permission_denied)
                    .setMessage(promptResId)
                    .setPositiveButton(R.string.permission_deny, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                dialog.dismiss();
                            } catch (Exception ignore) {
                            }
                            setUserDeniedPermissionAfterRationale(permission);
                            mRequestPermissionsInProcess.set(false);
                        }
                    })
                    .setNegativeButton(R.string.permission_retry, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                dialog.dismiss();
                            } catch (Exception ignore) {
                            }
                            mRequestPermissionsInProcess.set(false);
                            checkPermissions(new String[]{permission});
                        }
                    })
                    .show();
        } else {
            mRequestPermissionsInProcess.set(false);
        }
    }

    private boolean userDeniedPermissionAfterRationale(String permission) {
        SharedPreferences sharedPrefs = getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE);
        return sharedPrefs.getBoolean(PREFERENCE_PERMISSION_DENIED + permission, false);
    }

    private void setUserDeniedPermissionAfterRationale(String permission) {
        SharedPreferences.Editor editor = getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE).edit();
        editor.putBoolean(PREFERENCE_PERMISSION_DENIED + permission, true).commit();
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                // Log.v(TAG,"Permission is granted");
                return true;
            } else {

                // Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            // Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    public void getContacts() {




        if (ActivityCompat.checkSelfPermission(ICE.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ICE.this, new String[]{Manifest.permission.READ_CONTACTS},
                    READ_CONTACT);

        }
        if (isStoragePermissionGranted()) {


            contactList = new ArrayList<Contact>();

            String phoneNumber = null;
            String email = null;

            Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
            String _ID = ContactsContract.Contacts._ID;
            String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
            String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

            Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
            String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

            Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
            String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
            String DATA = ContactsContract.CommonDataKinds.Email.DATA;

            StringBuffer output;

            ContentResolver contentResolver = getContentResolver();

            cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

            // Iterate every contact in the phone
            if (cursor.getCount() > 0) {

                counter = 0;
                while (cursor.moveToNext()) {
                    output = new StringBuffer();

                    // Update the progress message
                    updateBarHandler.post(new Runnable() {
                        public void run() {
                            pDialog.setMessage("Reading contacts : " + counter++ + "/" + cursor.getCount());
                        }
                    });

                    String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                    String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                    if (hasPhoneNumber > 0) {

                        output.append("\n First Name:" + name);

                        //This is to read multiple phone numbers associated with the same contact
                        Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);

                        while (phoneCursor.moveToNext()) {
                            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                            output.append("\n Phone number:" + phoneNumber);

                        }

                        phoneCursor.close();

                        // Read every email id associated with the contact
                        Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{contact_id}, null);

                        while (emailCursor.moveToNext()) {

                            email = emailCursor.getString(emailCursor.getColumnIndex(DATA));

                            output.append("\n Email:" + email);

                        }

                        emailCursor.close();
                    }

                    // Add the contact to the ArrayList
                    if (phoneNumber != null)
                        contactList.add(new Contact(name, phoneNumber));
                }

                // ListView has to be updated using a ui thread
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        //databaseReference.child("contact").child(userId).setValue(contactList);

                        ContactAdapter adapter = new ContactAdapter(getApplicationContext(), R.layout.contact_item, contactList, prefHelper);
                        mListView.setAdapter(adapter);
                    }
                });


                // Dismiss the progressbar after 500 millisecondds
                updateBarHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        pDialog.cancel();
                    }
                }, 500);
            }
        }

    }

}