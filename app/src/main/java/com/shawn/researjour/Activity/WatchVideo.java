package com.shawn.researjour.Activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shawn.researjour.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class WatchVideo extends AppCompatActivity {

    Toolbar watchVideoToolbar;
    VideoView videoView;
    Uri videoUri;
    MediaController mediaController;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_video);

        //get id of post using intent
        Intent intent=getIntent();
        final String postId = intent.getStringExtra("postId");

        //toolbar
        watchVideoToolbar = findViewById(R.id.watchVideoToolbar_id);
        setSupportActionBar(watchVideoToolbar);
        getSupportActionBar().setTitle("Watch Video");

        databaseReference= FirebaseDatabase.getInstance().getReference("Video");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    String videoUriLocation=""+ds.child(postId).getValue();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        videoUri= Uri.parse("https://firebasestorage.googleapis.com/v0/b/researjour.appspot.com/o/videoUploads%2Fvideo_1584338829050?alt=media&token=57a0da52-a568-4a9d-ab7c-e8361d31d4a8");


        videoView=findViewById(R.id.videoView_id);
        videoView.setVideoURI(videoUri);
        videoView.requestFocus();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        mediaController=new MediaController(WatchVideo.this);
                        videoView.setMediaController(mediaController);
                        mediaController.setAnchorView(videoView);
                    }
                });

            }
        });
        videoView.start();

    }
}
