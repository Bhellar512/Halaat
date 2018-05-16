package com.halaat.halaat.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.halaat.halaat.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import Modal.User;
import helpers.BasePreferenceHelper;

public class SignIn extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    Button register, signin, fbBtn, gmailBtn;
    EditText email, password;
    private FirebaseAuth mAuth;
    private String TAG = "Login Activity";
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 007;
    DatabaseReference databaseReference;
    BasePreferenceHelper prefHelper;

    private final static int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 789;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        prefHelper =new BasePreferenceHelper(getApplicationContext());

        if(prefHelper.isLogin()){
            Intent intent = new Intent(SignIn.this, WelcomeScreen.class);
            intent.putExtra("UserObject", prefHelper.getUserId());
            startActivity(intent);
        }

        linkXML();
        setListener();
        work();
    }

    public void linkXML() {
        register = findViewById(R.id.register);
        signin = findViewById(R.id.signin);
        email = findViewById(R.id.email);
        password = findViewById(R.id.pass);
        fbBtn = findViewById(R.id.facebook);
        gmailBtn = findViewById(R.id.gmail);
    }

    public void setListener() {
        register.setOnClickListener(this);
        signin.setOnClickListener(this);
    }

    public void work() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        FacebookSdk.sdkInitialize(getApplicationContext());
        fbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkMultipleRunTimePermission()) {
                        initial();
                    }
                } else {
                    initial();
                }
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(SignIn.this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        gmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkMultipleRunTimePermission()) {
                    signIn();

                } else {
                    signIn();
                }
            }
        });

    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.register) {
            Intent intent = new Intent(SignIn.this, Register.class);
            startActivity(intent);
        }
        if (view.getId() == R.id.signin) {
            requestPermission();

        }
    }

    //In this method we ask runtime permissions
    public void requestPermission() {

        String getEmail = email.getText().toString().trim();
        String getPass = password.getText().toString().trim();
        if (!getEmail.isEmpty()) {
            if (!getPass.isEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkMultipleRunTimePermission()) {
                        signIn(getEmail, getPass);
                    }
                } else {
                    signIn(getEmail, getPass);
                }

            } else {
                email.requestFocus();
                email.setError("please provide password");
            }

        } else {
            email.requestFocus();
            email.setError("please provide email");
        }
    }

    private boolean checkMultipleRunTimePermission() {
        int permissionFineLocation = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> permissionsNeeded = new ArrayList<String>();
        if (permissionFineLocation != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                if (grantResults.length > 0) {
                    boolean fineLocationPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (fineLocationPermission) {
                        // Permissions are granted

                    } else {
                        Snackbar.make(this.findViewById(android.R.id.content),
                                "Please Grant Permissions to use this app",
                                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {requestPermissions
                                        (
                                                new String[]{
                                                        Manifest.permission.ACCESS_FINE_LOCATION},
                                                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                                    }
                                }).show();
                    }
                }
                break;
        }
    }


    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }


    public void signIn(final String mail, final String pass) {
        mAuth.signInWithEmailAndPassword(mail, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            prefHelper.setLoginStatus(true);
                            Intent intent = new Intent(SignIn.this, WelcomeScreen.class);
                            intent.putExtra("UserObject", user.getUid());
                            prefHelper.setUserId(user.getUid()+"");
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            SignIn.this.finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignIn.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void initial() {

        callbackManager = CallbackManager.Factory.create();
//
//        LoginManager.getInstance().logInWithReadPermissions(this,
//                Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                String accessToken = loginResult.getAccessToken().getToken();
                Log.i("accessToken", accessToken);

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i(TAG, response.toString());
                        // Get facebook data from login
                        Bundle bFacebookData = getFacebookData(object);


                        final ProgressDialog sweetAlertDialog = new ProgressDialog(getApplicationContext());
                        sweetAlertDialog.setTitle("Signing in");
                        sweetAlertDialog.show();
                        final String id = bFacebookData.getString("id");
                        final String name = bFacebookData.getString("first_name");
                        final String lName = bFacebookData.getString("last_name");
                        final String signupEmail = bFacebookData.getString("email");
                        Log.e("thisisdata", id);

                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // Parámetros que pedimos a facebook
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                System.out.println("onCancel");
                Log.e("thisisdata", "onCancel");

            }

            @Override
            public void onError(FacebookException exception) {
                System.out.println("onError");
                try {
                    Log.e("thisisdata", "onError");
                    Log.v(TAG, exception.getCause().toString());
                } catch (Exception ex) {
                    Log.e("thisisdata", ex.getMessage());
                }
            }
        });
    }

    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");
            bundle.putString("id", id);

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));

            Log.d(TAG, bundle.getString("email"));
            Log.d(TAG, object + "");

            return bundle;
        } catch (JSONException e) {
            Log.d(TAG, "Error parsing JSON");
        }
        return null;
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            final GoogleSignInAccount acct = result.getSignInAccount();

//            Log.e(TAG, "display name: " + acct.getDisplayName());

//            String personName = acct.getDisplayName();
//            String personPhotoUrl = acct.getPhotoUrl().toString();
            final String id = acct.getId();
            final String name = acct.getGivenName();
            final String lName = acct.getFamilyName();
            final String signupEmail = acct.getEmail();

            signUp(signupEmail, id, name, "0", "Karachi");

        } else {
            // Signed out, show unauthenticated UI.
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            // Pass the activity result back to the Facebook SDK
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onBackPressed() {

    }

    public void signUp(final String mail, final String pass, final String name, final String mobile, final String city) {
        mAuth.createUserWithEmailAndPassword(mail, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String key = user.getUid();
                            User userModal = new User(name, mail, key, pass, mobile, city);
                            databaseReference.child("Users").child(key).setValue(userModal);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignIn.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
