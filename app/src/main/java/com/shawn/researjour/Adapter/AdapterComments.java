package com.shawn.researjour.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shawn.researjour.Models.ModelComment;
import com.shawn.researjour.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterComments extends RecyclerView.Adapter<AdapterComments.MyHolder> {

    Context context;
    List<ModelComment> commentList;
    String myUid, postId;

    public AdapterComments(Context context, List<ModelComment> commentList, String myUid, String postId) {
        this.context = context;
        this.commentList = commentList;
        this.myUid = myUid;
        this.postId = postId;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.show_comment_layout, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        //get the data
        final String uid=commentList.get(i).getUid();
        String name=commentList.get(i).getuName();
        String email=commentList.get(i).getuEmail().toString();
        String image=commentList.get(i).getuDp();
        final String cid=commentList.get(i).getcId();
        String comment=commentList.get(i).getComment();
        String timestamp=commentList.get(i).getTimestamp();
        String isExpert=commentList.get(i).getIsExpert();

        //convert timestamp to dd/mm/yyyy hh:mm am/pm
        Calendar calendar=Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String pTimeFormat= DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        //set the data
        myHolder.nameTv.setText(name);
        myHolder.timeStampTv.setText(pTimeFormat);
        myHolder.commentTv.setText(comment);

        if (isExpert=="yes"){
            myHolder.expertIcon.setVisibility(View.VISIBLE);
        }

        //set user picture
        try {
            Picasso.get().load(image).placeholder(R.drawable.user_profile).into(myHolder.avatarIv);
        }catch (Exception e){}

        //comment click listener
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if this comment is by currently signed in user or not
                if (myUid.equals(uid)){
                    //my comment
                    //show delete dialog
                    AlertDialog.Builder builder=new AlertDialog.Builder(v.getRootView().getContext());
                    builder.setTitle("Delete Feedback");
                    builder.setMessage("Are you sure to delete this feedback?");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteFeedback(cid);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    //show dialog
                    builder.create().show();
                }else {
                    Toast.makeText(context, "Can't Delete other's feedback", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void deleteFeedback(String cid) {
        final DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        ref.child("Comments").child(cid).removeValue();

        //now update the comments count
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String comments=""+dataSnapshot.child("pComments").getValue();
                int newCommentValue=Integer.parseInt(comments)-1;
                ref.child("pComments").setValue(""+newCommentValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }


    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{


        //views from research_post_layout.xml
        ImageView avatarIv,expertIcon;
        TextView nameTv,commentTv,timeStampTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            avatarIv=itemView.findViewById(R.id.uImage_id);
            expertIcon=itemView.findViewById(R.id.expert_id);
            nameTv=itemView.findViewById(R.id.uNameTv_id);
            commentTv=itemView.findViewById(R.id.commentTv_id);
            timeStampTv=itemView.findViewById(R.id.timeStamp_id);
        }
    }


}
