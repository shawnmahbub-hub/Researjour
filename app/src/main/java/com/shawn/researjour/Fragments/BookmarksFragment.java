package com.shawn.researjour.Fragments;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shawn.researjour.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarksFragment extends Fragment {

    private Toolbar mTopToolbar;
    //components part
    Toolbar newPostToolbar;
    private CircleImageView postProfilePicture;
    private TextView postUserName, postTime,pdfName;
    private EditText researchTitle,abstraction,videoLink;
    private ImageButton addPdf;
    private Button postButton;
    private Uri pdfUri;

    //get some info of the user
    String name,email,uid,dp;

    //firebase part
    private DatabaseReference UsersRef;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseDatabase database;


    public BookmarksFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.fragment_bookmarks,container,false);

        //finding the id of the widgets in the add post activity
        postProfilePicture=(CircleImageView)view.findViewById(R.id.postProfileImage_id);
        postUserName=(TextView)view.findViewById(R.id.postProfileName_id);
        postTime=(TextView)view.findViewById(R.id.postTimeText_id);
        researchTitle=(EditText) view.findViewById(R.id.postTitleText_id);
        abstraction=(EditText)view.findViewById(R.id.postabstractionText_id);
        videoLink=(EditText)view.findViewById(R.id.videoLinkET_id);
        addPdf=(ImageButton)view.findViewById(R.id.addPdf_id);
        pdfName=view.findViewById(R.id.pdfTextView_id);
        postButton=view.findViewById(R.id.post_button_id);

        //firebase part
        mAuth= FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        database=FirebaseDatabase.getInstance();

        //get some info of current user to include in post
        UsersRef = FirebaseDatabase.getInstance().getReference("Users");
        Query query=UsersRef.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    name=""+dataSnapshot1.child("fullname").getValue();
                    email=""+dataSnapshot1.child("email").getValue();
                    dp=""+dataSnapshot1.child("profileimage").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String currentUserID = mAuth.getCurrentUser().getUid();
        /*fetching the user profile image and user name from fireBase*/
        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("fullname"))
                    {
                        String fullname = dataSnapshot.child("fullname").getValue().toString();
                        postUserName.setText(fullname);

                    }
                    if(dataSnapshot.hasChild("profileimage"))
                    {
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.user_profile).into(postProfilePicture);
                    }
                    if (dataSnapshot.hasChild("university")){
                        String universityName = dataSnapshot.child("university").getValue().toString();
                        postTime.setText(universityName);
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

        //open file storage when user wanted to post pdf file
        addPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission
                        (getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED)
                {
                    selectPdf();
                }else {
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);
                }
            }
        });

        //button for posting the new research information to the fireBase
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFundInputs();
            }
        });

        return view;
    }

    //registration inputs validation inputs
    private void validateFundInputs() {
        String titleInput = researchTitle.getText().toString().trim();
        String abstractionInput=abstraction.getText().toString().trim();
        String videoLinkInput=videoLink.getText().toString().trim();

        if(TextUtils.isEmpty(titleInput)){
            Animation animShake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
            researchTitle.startAnimation(animShake);
            Toast.makeText(getActivity(), "Enter Title..", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(abstractionInput)){
            Animation animShake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
            abstraction.startAnimation(animShake);
            Toast.makeText(getActivity(), "Write abstraction..", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(videoLinkInput)){
            Animation animShake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
            videoLink.startAnimation(animShake);
            Toast.makeText(getActivity(), "Paste YouTube Video Link..", Toast.LENGTH_SHORT).show();
            return;
        }else {
            //pdf file
            if (pdfUri!=null){
                uploadFile(pdfUri);
            }
            //registration method
            registerFund(titleInput,abstractionInput,videoLinkInput);
        }
    }
    private void selectPdf() {
        //method for selecting the pdf file from storage
        Intent intent=new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,86);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==86 && resultCode==RESULT_OK && data!=null)
        {
            pdfUri=data.getData();
            pdfName.setText("file: "+data.getData().getLastPathSegment());
        }
    }

    private void uploadFile(Uri pdfUri) {
        final String pdftimeStamp=String.valueOf(System.currentTimeMillis());

        final String fileName="pdf_"+pdftimeStamp;
        StorageReference storageReference=storage.getReference();

        storageReference.child("pdfUploads").child("FundPaper").child(fileName).putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        String url=taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                        DatabaseReference databaseReference=database.getReference();

                        databaseReference.child("pdf").child(fileName).setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                }else {
                                    Toast.makeText(getActivity(), "your research pdf not uploaded successfully", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "your research pdf not uploaded successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerFund(String titleInput, String abstractionInput, final String videoLinkInput) {
        //showing the progress loading bar
        final ProgressDialog loadingBar;
        loadingBar=new ProgressDialog(getActivity());
        loadingBar.setTitle("Applying Fund");
        loadingBar.setMessage("Wait for a moment while we are registering your funding information");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(false);

        //for post-image name, post-id, post-publish time
        final String timeStamp=String.valueOf(System.currentTimeMillis());

        //uri is received upload post to firebase database
        HashMap<Object,String> hashMap=new HashMap<>();
        //put post info
        hashMap.put("uid", uid);
        hashMap.put("uName",name);
        hashMap.put("uDp",dp);
        hashMap.put("postid",timeStamp);
        hashMap.put("uEmail",email);
        hashMap.put("title", titleInput);
        hashMap.put("abstraction", abstractionInput);
        hashMap.put("videoLink", videoLinkInput);
        hashMap.put("pTime",timeStamp);

        //path to store post data
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Fund Requisition");

        //put data in this database reference
        databaseReference.child(timeStamp).setValue(hashMap).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        loadingBar.dismiss();
                        //added in database
                        Toast.makeText(getActivity(), "Fund Requisition Applied", Toast.LENGTH_SHORT).show();
                        //reset views
                        researchTitle.setText("");
                        abstraction.setText("");
                        videoLink.setText("");

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getActivity(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        mTopToolbar=getActivity().findViewById(R.id.toolbar);
        mTopToolbar.setVisibility(View.GONE);
        super.onCreate(savedInstanceState);

    }

}
