package com.shawn.researjour.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shawn.researjour.Adapter.AdapterComments;
import com.shawn.researjour.Models.ModelComment;
import com.shawn.researjour.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetailActivity extends AppCompatActivity {

    //to get details of user
    String hisUid,myUid,myEmail,myName,myDp,postId,pLikes,hisDp,hisName,pImage;
    boolean mProcessComment=false;

    //progress dialog
    ProgressDialog pd;
    //views
    Toolbar newPostToolbar;
    ImageView uPictureIv,pImageIv;
    TextView uNameTv,pTimeTiv,pTitleTv,pAbstractionTv,pVideoLinkTv,pVideoLinkText;
    ImageButton moreBtn;
    LinearLayout profileLayout;
    RecyclerView recyclerView;

    List<ModelComment> commentList;
    AdapterComments adapterComments;

    //add comment views
    EditText commentEt;
    ImageButton sendBtn;
    CircleImageView cAvatarIv;

    FirebaseStorage firebaseStorage;
    StorageReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        //toolbar
        newPostToolbar = findViewById(R.id.postDetailToolbar_id);
        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setTitle("Research Post");

        //get id of post using intent
        Intent intent=getIntent();
        postId=intent.getStringExtra("postId");


        //init views
        uPictureIv=findViewById(R.id.postDetailHomeProfileImage_id);
        pImageIv=findViewById(R.id.postDetailResearchPostImage_id);
        uNameTv=findViewById(R.id.postDetailHomeResearcherName_id);
        pTimeTiv=findViewById(R.id.postDetailHomePostTime_id);
        pTitleTv=findViewById(R.id.postDetailHomePostTitleText_id);
        pAbstractionTv=findViewById(R.id.postDetailHomePostDescText_id);
        pVideoLinkTv=findViewById(R.id.postDetailVideoLink_id);
        pVideoLinkText=findViewById(R.id.postDetailVideoLinkText_id);
        moreBtn=findViewById(R.id.postDetailMoreButton_id);
        profileLayout=findViewById(R.id.profileLayout);
        recyclerView=findViewById(R.id.recyclerView_id);

        //init add comment views
        cAvatarIv=findViewById(R.id.addCommentUserImageView_id);
        commentEt=findViewById(R.id.commentEditText_id);
        sendBtn=findViewById(R.id.sendComment_id);

        checkUserStatus();

        loadPostInfo();
        loadUserInfo();

        //sendBtn set on click listener
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });

        //more button click listener
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreOptions();
            }
        });


        loadComments();

    }

    private void loadComments(){
        //layout for recyclerview
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        //set layout to recyclerview
        recyclerView.setLayoutManager(layoutManager);

        //init comment list
        commentList=new ArrayList<>();

        //path of the post, to get it's comments
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelComment modelComment=ds.getValue(ModelComment.class);
                    commentList.add(modelComment);

                    //setup adapter
                    adapterComments=new AdapterComments(getApplicationContext(),commentList,myUid,postId);
                    //set adapter
                    recyclerView.setAdapter(adapterComments);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showMoreOptions()  {
        //creating popup menu currently having option delete, we will add more options later
        PopupMenu popupMenu=new PopupMenu(this, moreBtn, Gravity.END);

        //show delete option in only post of currently signed-in user
        if (hisUid.equals(myUid)){
            //add item in menu
            popupMenu.getMenu().add(Menu.NONE,1,0,"Edit Post");
            popupMenu.getMenu().add(Menu.NONE,0,0,"Delete Post");
        }
        //menu item click listener
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id=menuItem.getItemId();
                if (id==0){
                    //delete is clicked
                    beginDelete();
                }else if (id==1){
                    //edit is clicked
                    /*start add new post activity with key "edit post" and the id of the post clicked*/
                    Intent intent=new Intent(PostDetailActivity.this, AddNewPost.class);
                    intent.putExtra("key","editPost");
                    intent.putExtra("editPostId",postId);
                    startActivity(intent);
                }
                return false;
            }
        });
        //show menu
        popupMenu.show();
    }

    private void beginDelete() {
        if (pImage.equals("noImage")){
            //post without image
            deleteWithoutImage();
        }else {
            //post with image
            deleteWithImage();
        }
    }

    private void deleteWithImage() {
        //progress bar
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Deleting Post..");
        /*steps:
         * 1.Delete Image using url
         * 2.Delete from database using post id*/
        StorageReference picRef= FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //image deleted
                Query fquery=FirebaseDatabase.getInstance().getReference("Posts")
                        .orderByChild("postid").equalTo(postId);
                fquery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            dataSnapshot1.getRef().removeValue();//remove valued from firebase where pid matches
                        }
                        //deleted
                        Toast.makeText(PostDetailActivity.this, "Research Post Deleted", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed
                pd.dismiss();
                Toast.makeText(PostDetailActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void deleteWithoutImage() {
        //progress bar
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Deleting Post..");

        Query fquery=FirebaseDatabase.getInstance().getReference("Posts")
                .orderByChild("postid").equalTo(postId);
        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    ds.getRef().removeValue();
                }
                //deleted
                Toast.makeText(PostDetailActivity.this, "Research Post Deleted", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void postComment() {
        pd=new ProgressDialog(this);
        pd.setMessage("Adding Feedback..");

        //get data from comment edit text
        String comment=commentEt.getText().toString().trim();
        //validate
        if (TextUtils.isEmpty(comment)){
            Toast.makeText(this, "Comment is Empty...", Toast.LENGTH_SHORT).show();
            return;
        }
        String timeStamp=String.valueOf(System.currentTimeMillis());

        //each post will have a child "Comments" that will contain comments of that post
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
        HashMap<String, Object> hashMap=new HashMap<>();
        hashMap.put("cId",timeStamp);
        hashMap.put("comment",comment);
        hashMap.put("timestamp",timeStamp);
        hashMap.put("uid",myUid);
        hashMap.put("uEmail",myEmail);
        hashMap.put("uDp",myDp);
        hashMap.put("uName",myName);

        //put this data in db
        ref.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //added
                        pd.dismiss();
                        Toast.makeText(PostDetailActivity.this, "Feedback added...", Toast.LENGTH_SHORT).show();
                        commentEt.setText("");
                        //method for counting comments
                        updateCommentCount();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed, not added
                pd.dismiss();
                Toast.makeText(PostDetailActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateCommentCount() {
        //increase comment count similar to the like counts
        mProcessComment=true;
        final DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mProcessComment){
                    String comments=""+dataSnapshot.child("pComments").getValue();
                    int newCommentValue=Integer.parseInt(comments)+1;
                    ref.child("pComments").setValue(""+newCommentValue);
                    mProcessComment=false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadUserInfo() {
        //get current user info
        Query myref=FirebaseDatabase.getInstance().getReference("Users");
        myref.orderByChild("uid").equalTo(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    myName=""+ds.child("fullname").getValue();
                    myDp=""+ds.child("profileimage").getValue();

                    //set data
                    try {
                        //if image is received then set
                        Picasso.get().load(myDp).placeholder(R.drawable.user_profile).into(cAvatarIv);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.user_profile).into(cAvatarIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadPostInfo()  {
        //get post using the id of the post
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Posts");
        Query query=ref.orderByChild("postid").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    //get Data
                    String pTitle=""+ds.child("title").getValue();
                    String pAbs=""+ds.child("abstraction").getValue();
                    String pVideo=""+ds.child("videoLink").getValue();
                    pLikes=""+ds.child("pLikes").getValue();
                    String pTimeStamp=""+ds.child("pTime").getValue();
                    String pImage=""+ds.child("postimage").getValue();
                    hisDp=""+ds.child("uDp").getValue();
                    hisUid=""+ds.child("uid").getValue();
                    String uEmail=""+ds.child("uEmail");
                    hisName=""+ds.child("uName").getValue();
                    String commentCount=""+ds.child("pComments").getValue();

                    //convert timestamp to dd/mm/yyyy hh:mm am/pm
                    Calendar calendar=Calendar.getInstance(Locale.getDefault());
                    calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
                    String pTime= DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();

                    //set data
                    pTitleTv.setText(pTitle);
                    pAbstractionTv.setText(pAbs);
                    pVideoLinkTv.setText(pVideo);
                    pTimeTiv.setText(pTime);
                    uNameTv.setText(hisName);

                    //set image of the user who posted
                    //if there is no image i.e. pImage.equals("noImage")the hide ImageView
                    if (pImage.equals("noImage")){
                        //hide imageView
                        pImageIv.setVisibility(View.GONE);
                    }else {
                        //shown imageView
                        pImageIv.setVisibility(View.VISIBLE);
                        try {
                            Picasso.get().load(pImage).into(pImageIv);
                        }catch (Exception e){
                        }
                    }

                    //set user Image in comment part
                    try {
                        Picasso.get().load(hisDp).placeholder(R.drawable.user_profile).into(uPictureIv);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.user_profile).into(uPictureIv);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkUserStatus(){
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            //user is signed in
            myEmail=user.getEmail();
            myUid=user.getUid();
        }else {
            //user not signed in, go to login activity
            startActivity(new Intent(this,Login.class));
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        if (id==android.R.id.home){
            sendUserToHomeActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    //intent for sending the user to the home activity when click the back button
    private void sendUserToHomeActivity()  {
        Intent intent=new Intent(this, Home.class);
        startActivity(intent);
    }
}