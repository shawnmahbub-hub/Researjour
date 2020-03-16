package com.shawn.researjour.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shawn.researjour.Activity.Welcome_Screen;
import com.shawn.researjour.Adapter.PostsAdapter;
import com.shawn.researjour.Models.ModelClassPost;
import com.shawn.researjour.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyResearchFragment extends Fragment {

    private Toolbar mTopToolbar;
    RecyclerView myResearchRecyclerView;

    List<ModelClassPost> postList;
    PostsAdapter postsAdapter;
    String uid;
    private FirebaseAuth firebaseAuth;


    public MyResearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view=inflater.inflate(R.layout.fragment_my_research,container,false);
        myResearchRecyclerView=view.findViewById(R.id.portfolioRecyclerView_id);
        firebaseAuth=FirebaseAuth.getInstance();

        postList=new ArrayList<>();

        checkUserStatus();

        loadMyPosts();

        return view;
    }

    private void loadMyPosts() {
        //linear layout for recyclerview
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
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
                    postsAdapter=new PostsAdapter(getActivity(),postList);
                    //set this adapter to recyclerview
                    myResearchRecyclerView.setAdapter(postsAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error: "+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserStatus(){
        //get current user
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if (user!=null){
            uid=user.getUid();
        }else {
            startActivity(new Intent(getActivity(), Welcome_Screen.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        mTopToolbar=getActivity().findViewById(R.id.toolbar);
        mTopToolbar.setVisibility(View.GONE);
        super.onCreate(savedInstanceState);
    }

}