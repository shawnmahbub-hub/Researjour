package com.shawn.researjour.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

public class HomeFragment extends Fragment {

    //firebase part
    FirebaseAuth firebaseAuth;

    RecyclerView recyclerView;
    List<ModelClassPost> postList;
    PostsAdapter postsAdapter;
    private Toolbar mTopToolbar;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_home, container, false);

        //init firebase
        firebaseAuth=FirebaseAuth.getInstance();
        postList=new ArrayList<>();

        //recycler view and its properties
        recyclerView=view.findViewById(R.id.postRecyclerView_id);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        //show newest post first, for this load from last
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        //set layout to recycler view
        recyclerView.setLayoutManager(layoutManager);

        //init post list
        loadPosts();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        mTopToolbar=getActivity().findViewById(R.id.toolbar);
        mTopToolbar.setVisibility(View.VISIBLE);
        super.onCreate(savedInstanceState);
    }

    private void loadPosts() {
        //path of all posts
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Posts");
        //get all data from this ref
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    ModelClassPost modelClassPost=dataSnapshot1.getValue(ModelClassPost.class);

                    postList.add(modelClassPost);

                    //adapter
                    postsAdapter=new PostsAdapter(getActivity(), postList);
                    //set adapter to recyclerview
                    recyclerView.setAdapter(postsAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //in case of error
                //Toast.makeText(getActivity(), "Error: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}