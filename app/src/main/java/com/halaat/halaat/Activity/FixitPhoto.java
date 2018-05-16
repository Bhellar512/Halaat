package com.halaat.halaat.Activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.halaat.halaat.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import Modal.Image;

public class FixitPhoto extends AppCompatActivity {
    String Storage_Path = "Images/";
    ImageButton upload;
    Uri uri;
    EditText description;
    private final static int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 789;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    Button btnViewPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixit_photo);
        databaseReference = FirebaseDatabase.getInstance().getReference("images");
        storageReference = FirebaseStorage.getInstance().getReference();
        description = findViewById(R.id.description);
        btnViewPhoto = (Button) findViewById(R.id.btn_viewPhoto);
        upload = findViewById(R.id.camera_button);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (checkMultipleRunTimePermission()) {
                        startActivityForResult(pickPhoto, 0);


                    }
                } else {
                    startActivityForResult(pickPhoto, 0);

                }

            }
        });

        btnViewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FixitPhoto.this,ViewPhotosActivity.class);
                startActivity(intent);
            }
        });
    }

    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }


    public void submit(View v) {

        if (description.getText().toString().isEmpty()) {

            Toast.makeText(FixitPhoto.this, "Please Provide Description", Toast.LENGTH_SHORT).show();
        } else {
            if (uri == null) {
                Toast.makeText(FixitPhoto.this, "Please Upload Picture", Toast.LENGTH_SHORT).show();
            } else {

                StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(uri));

                // Adding addOnSuccessListener to second StorageReference.
                storageReference2nd.putFile(uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                // Getting image name from EditText and store into string variable.
//                            String TempImageName = ImageName.getText().toString().trim();

                                // Hiding the progressDialog after done uploading.

                                // Showing toast message after done uploading.

                                Image img = new Image(description.getText().toString(), taskSnapshot.getDownloadUrl().toString());
                                databaseReference.push().setValue(img);
                                Toast.makeText(FixitPhoto.this, "Image Saved", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        // If something goes wrong .
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {

                                // Hiding the progressDialog.

                                // Showing exception erro message.
                                Toast.makeText(FixitPhoto.this, exception.getMessage(), Toast.LENGTH_LONG).show();

                            }
                        })

                        // On progress change upload time.
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                // Setting progressDialog Title.

                            }
                        });
            }
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                uri = getImageUri(getApplicationContext(), (Bitmap) imageReturnedIntent.getExtras().get("data"));
                upload.setImageURI(uri);
            }

        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private boolean checkMultipleRunTimePermission() {
        int permissionCamera = ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA);

        List<String> permissionsNeeded = new ArrayList<String>();

        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(FixitPhoto.this,
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
                    boolean cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeExternalPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraPermission && writeExternalPermission) {
                        // Permissions are granted

                    } else {
                        Snackbar.make(FixitPhoto.this.findViewById(android.R.id.content),
                                "Please Grant Permissions to use this app",
                                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                                new View.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    @Override
                                    public void onClick(View v) {
                                        requestPermissions(
                                                new String[]{
                                                        Manifest.permission.CAMERA,
                                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,

                                                },
                                                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                                    }
                                }).show();
                    }
                }
                break;
        }
    }

}
