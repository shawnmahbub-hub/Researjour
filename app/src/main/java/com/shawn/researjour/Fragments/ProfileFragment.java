package com.shawn.researjour.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shawn.researjour.Activity.Login;
import com.shawn.researjour.R;
import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private Toolbar mTopToolbar;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //views from xml
    CircleImageView profileImageView;
    TextView userName,researcherRole,university,dob_text,gender,logoutBtn,aboutApp;



    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view=inflater.inflate(R.layout.fragment_profile,container,false);

        //init fireBase
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");


        //init views from xml components
        profileImageView=view.findViewById(R.id.profileFragment_Picture_id);
        userName=view.findViewById(R.id.profileFragment_username_id);
        university=view.findViewById(R.id.profileUniversityText_id);
        researcherRole=view.findViewById(R.id.profileRoleText_id);
        gender=view.findViewById(R.id.profileGenderText_id);
        dob_text=view.findViewById(R.id.profile_dob_id);
        logoutBtn=view.findViewById(R.id.logout_button_id);
        aboutApp=view.findViewById(R.id.about_button_id);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(getActivity(), Login.class));
                Snackbar snackbar=Snackbar.make(getActivity().findViewById(R.id.login_Activity),R.string.logout,Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });

        aboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //alert dialog of about app
            }
        });

        //retrieving data from fireBase
        String currentUserID = firebaseAuth.getCurrentUser().getUid();
        /*fetching the user profile image and user name from fireBase*/
        databaseReference.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("fullname"))
                    {
                        String fullname = dataSnapshot.child("fullname").getValue().toString();
                        userName.setText(fullname);

                    }
                    if(dataSnapshot.hasChild("profileimage"))
                    {
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile_image).into(profileImageView);
                    }
                    if(dataSnapshot.hasChild("date of birth"))
                    {
                        String dobInput = dataSnapshot.child("date of birth").getValue().toString();
                        dob_text.setText(dobInput);
                    }
                    if (dataSnapshot.hasChild("university")){
                        String universityName = dataSnapshot.child("university").getValue().toString();
                        university.setText(universityName);
                    }if (dataSnapshot.hasChild("researcherRole")){
                    String researcherRoleInput = dataSnapshot.child("researcherRole").getValue().toString();
                    researcherRole.setText(researcherRoleInput);
                    }
                    if (dataSnapshot.hasChild("gender")){
                        String genderInput = dataSnapshot.child("gender").getValue().toString();
                        gender.setText(genderInput);
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "Profile name do not exists...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        mTopToolbar=getActivity().findViewById(R.id.toolbar);
        mTopToolbar.setVisibility(View.GONE);
        super.onCreate(savedInstanceState);
    }
}
