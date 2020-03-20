package com.shawn.researjour.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shawn.researjour.Adapter.PostsAdapter;
import com.shawn.researjour.Models.ModelClassPost;
import com.shawn.researjour.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ThereProfileActivity extends AppCompatActivity {

    private Toolbar mTopToolbar;

    RecyclerView myResearchRecyclerView;

    List<ModelClassPost> postList;
    PostsAdapter postsAdapter;
    String uid;

    //fireBase
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    //views from xml
    TextView userName,uEmail,ResearcherRole,Useruniversity,gender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_there_profile);

        mTopToolbar = findViewById(R.id.thereProfileToolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setTitle("User Profile");

        //init views from xml components
        userName=findViewById(R.id.profileFragment_username_id);
        uEmail=findViewById(R.id.emailTv_id);
        Useruniversity=findViewById(R.id.profileUniversityText_id);
        ResearcherRole=findViewById(R.id.profileRoleText_id);
        gender=findViewById(R.id.profileGenderText_id);

        myResearchRecyclerView=findViewById(R.id.portfolioRecyclerView_id);
        firebaseAuth=FirebaseAuth.getInstance();

        //get uid of clicked user to retrieve his posts
        Intent intent=getIntent();
        uid=intent.getStringExtra("uid");

        Query query=FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check until required data get
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    //get data
                    String name=""+ds.child("fullname").getValue();
                    String email=""+ds.child("email").getValue();
                    String university=""+ds.child("university").getValue();
                    String researcherRole=""+ds.child("researcherRole").getValue();
                    String uGender=""+ds.child("gender").getValue();

                    //set data
                    userName.setText(name);
                    uEmail.setText(email);
                    Useruniversity.setText(university);
                    ResearcherRole.setText(researcherRole);
                    gender.setText(uGender);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        postList=new ArrayList<>();
        loadHisPosts();

    }

    private void loadHisPosts() {
        //linear layout for recyclerview
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        //show newest post first, for this load from last
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set this layout to recyclerview
        myResearchRecyclerView.setLayoutManager(layoutManager);

        //init posts list
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Posts");
        //query to load posts
        Query query=ref.orderByChild("uid").equalTo(uid);
        //get all data from this ref
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelClassPost myPosts=ds.getValue(ModelClassPost.class);

                    //add to list
                    postList.add(myPosts);
                    //adapter
                    postsAdapter=new PostsAdapter(ThereProfileActivity.this,postList);
                    //set this adapter to recyclerview
                    myResearchRecyclerView.setAdapter(postsAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ThereProfileActivity.this, "Error: "+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        if (id==android.R.id.home){
            sendUserToHomeActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendUserToHomeActivity() {
        Intent intent=new Intent(this, Home.class);
        startActivity(intent);
    }
}
