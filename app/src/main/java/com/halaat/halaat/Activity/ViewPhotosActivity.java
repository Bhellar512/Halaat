package com.halaat.halaat.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.GridView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.halaat.halaat.R;

import java.util.ArrayList;
import java.util.Map;

import Modal.Image;
import Views.ArrayListAdapter;
import Views.ViewPhotosBinder;
import helpers.InternetHelper;

public class ViewPhotosActivity extends Activity {

    GridView gvPhoto;
    private ArrayListAdapter<Image> adapter;
    DatabaseReference databaseReference;
    ArrayList<Image> collection;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewphoto);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        gvPhoto = (GridView) findViewById(R.id.gv_viewphotos);

        if (InternetHelper.CheckInternetConectivityandShowToast(ViewPhotosActivity.this))
            getData();
    }

    private void getData() {

        databaseReference.child("images").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                collectReports((Map<String, Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void collectReports(Map<String, Object> reports) {

        if (reports == null)
            return;
        collection = new ArrayList<>();
        //iterate through each reports
        for (Map.Entry<String, Object> entry : reports.entrySet()) {

            //Get user map
            Map report = (Map) entry.getValue();
            collection.add(new Image(report.get("description").toString(), report.get("uri").toString()));

        }

        setGridView(collection);

    }

    private void setGridView(ArrayList<Image> collection) {

        if (collection != null) {
            adapter = new ArrayListAdapter<Image>(ViewPhotosActivity.this, new ViewPhotosBinder(getApplicationContext(),this));

            adapter.clearList();
            gvPhoto.setAdapter(adapter);
            adapter.addAll(collection);
            adapter.notifyDataSetChanged();
        }
    }


}
