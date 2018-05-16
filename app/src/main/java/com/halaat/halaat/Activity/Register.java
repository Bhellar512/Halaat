package com.halaat.halaat.Activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.halaat.halaat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Modal.User;

public class Register extends AppCompatActivity implements View.OnClickListener {
    Button register;
    EditText name, email, password, mobile, city;
    private FirebaseAuth mAuth;
    private String TAG = "Register Activity";
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        linkXML();
        setListener();
        work();


    }

    public void linkXML() {
        register = findViewById(R.id.register);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.pass);
        mobile = findViewById(R.id.mobile);
        city = findViewById(R.id.city);
    }

    public void setListener() {
        register.setOnClickListener(this);
    }

    public void work() {
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.register) {
            String tempName = name.getText().toString();
            String tempMail = email.getText().toString();
            String tempPass = password.getText().toString();
            String tempMobile = mobile.getText().toString();
            String tempCity = city.getText().toString();
            if (tempMail.isEmpty()) {
                name.setError("Name is required!");
                name.setFocusable(true);
            } else if (tempMail.isEmpty()) {
                email.setError("Email is required!");
                email.setFocusable(true);
            } else if (tempPass.isEmpty()) {
                password.setError("Password is required!");
                password.setFocusable(true);
            } else if (tempMobile.isEmpty()) {
                mobile.setError("Mobile is required!");
                mobile.setFocusable(true);
            } else if (tempCity.isEmpty()) {
                city.setError("City is required!");
                city.setFocusable(true);
            } else {
                signUp(tempMail, tempPass, tempName,tempMobile,tempCity);
            }

        }
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
                            Toast.makeText(Register.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}
