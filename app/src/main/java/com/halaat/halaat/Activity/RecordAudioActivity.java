package com.halaat.halaat.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.halaat.halaat.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RecordAudioActivity extends AppCompatActivity {

    ImageButton ibRecord;
    TextView    textview;
    String AudiofilePath;
    RecyclerView recyclerView;
    boolean Recording=false;
    MediaRecorder mediaRecorder;
    StorageReference srAudio;
    DatabaseReference drAudio, drUser;
    FirebaseAuth mAuth;
    FirebaseUser fu;
    int ai=1;
    DatabaseReference audiodatabase;
    MediaPlayer mediaPlayer;
    public static final int RECORD_AUDIO = 0;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_audio);
        ibRecord = (ImageButton) findViewById(R.id.ibRecordButton);
        textview = (TextView) findViewById(R.id.tvRecordAudioView);
        recyclerView = findViewById(R.id.audiodisplay);
        Calendar c = Calendar.getInstance();
        System.out.println("Current Time"+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String formatdate= df.format(c.getTime());
        audiodatabase = FirebaseDatabase.getInstance().getReference().child("Upload Audio");
        srAudio= FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        fu = mAuth.getCurrentUser();
        drAudio= FirebaseDatabase.getInstance().getReference().child("Upload Audio");
        drUser = FirebaseDatabase.getInstance().getReference().child("Users").child(fu.getUid());

        AudiofilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+formatdate+"NewAudio.mp3";
        ibRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (ActivityCompat.checkSelfPermission(RecordAudioActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ) {

                    ActivityCompat.requestPermissions(RecordAudioActivity.this, new String[]{Manifest.permission.RECORD_AUDIO},
                            RECORD_AUDIO);

                } else if(isStoragePermissionGranted()) {


                if(event.getAction()==event.ACTION_DOWN)
                {

                    recordAudio();
                    textview.setText("Recording is Started...");
                }
                else
                if(event.getAction()==event.ACTION_UP)
                {
                    stopAudio();
                    textview.setText("Recording is Stopped");
                }
                }
                return false;
            }
        });

        recyclerView = findViewById(R.id.audiodisplay);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        onStart();
    }

    public void recordAudio ()
    {
        Recording = true;
        try
        {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mediaRecorder.setOutputFile(AudiofilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();

        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
    }

    public void stopAudio ()
    {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder=null;
        uploadAudio();
    }
    @Override
    public void onStart() {
        super.onStart();
        System.out.println("start");
        FirebaseRecyclerAdapter<DownloadingAudioList,downloadingaudio> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<DownloadingAudioList, downloadingaudio>(
                DownloadingAudioList.class,
                R.layout.audiodownloadfile,
                downloadingaudio.class,
                audiodatabase
        ) {
            @Override
            protected void populateViewHolder(downloadingaudio viewHolder, DownloadingAudioList model, int position) {
                System.out.println("populate");

                final String audio_key = getRef(position).getKey();
                viewHolder.setAudioTitle(model.getAudiotitle());
                viewHolder.setusername(model.getUsername());
                viewHolder.setname(getApplicationContext(),model.getAudioname());

                viewHolder.audioplay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        audiodatabase.child(audio_key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String audiofile = (String) dataSnapshot.child("audioname").getValue();


                                try {
                                    System.out.println("audiofile "+audiofile);
                                    mediaPlayer = new MediaPlayer();
                                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                    mediaPlayer.setDataSource(audiofile);
                                    mediaPlayer.prepare();
                                    mediaPlayer.start();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
    private void uploadAudio()
    {
        Uri uri= Uri.fromFile(new File(AudiofilePath));
        StorageReference filepath = srAudio.child("Upload Audio").child(uri.getLastPathSegment());
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                final DatabaseReference new_post = drAudio.push();

                drUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {

                        String downloadUri = taskSnapshot.getDownloadUrl().toString();
                        new_post.child("audiotitle").setValue(AudiofilePath);
                        new_post.child("userid").setValue(fu.getUid());
                        new_post.child("audioname").setValue(downloadUri);
                        new_post.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                               // startActivity(new Intent(RecordAudioActivity.this,RecordAudioActivity.class));


                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });
    }

    public static class downloadingaudio extends RecyclerView.ViewHolder {
        FloatingActionButton audioplay;

        View sview;
        public downloadingaudio(View itemView) {
            super(itemView);

            sview=itemView;
        }

        public void setAudioTitle(String audiofilename){

            TextView audioname = sview.findViewById(R.id.tvaudiofilename);
            audioname.setText(audiofilename);

        }

        public void setusername(String Username){

            System.out.println(Username);

            TextView username = sview.findViewById(R.id.tvaudiousername);
            username.setText(Username);

        }

        public void setname(Context context,String audioname){
            audioplay = sview.findViewById(R.id.fabAudioPlay);
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
               // Log.v(TAG,"Permission is granted");
                return true;
            } else {

               // Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
           // Log.v(TAG,"Permission is granted");
            return true;
        }
    }
}

