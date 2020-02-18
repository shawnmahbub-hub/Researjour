package com.shawn.researjour.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shawn.researjour.Models.ModelClassPost;
import com.shawn.researjour.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.MyHolder> {

    Context context;
    List<ModelClassPost> postList;

    public PostsAdapter(Context context, List<ModelClassPost> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        //inflate layout research post layout
        View view= LayoutInflater.from(context)
                .inflate(R.layout.research_post_layout,viewGroup,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {

        //get data
        String uid=postList.get(i).getUid();
        String uEmail=postList.get(i).getuEmail();
        String uName=postList.get(i).getuName();
        String uDp=postList.get(i).getuDp();
        String pTitle=postList.get(i).getTitle();
        String pAbstraction=postList.get(i).getAbstraction();
        String pImage=postList.get(i).getPostimage();
        String pTime=postList.get(i).getpTime();

        //convert timestamp to dd/mm/yyyy hh:mm am/pm
        Calendar calendar=Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTime));
        String pTimeFormat= DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        //set data
        myHolder.uNameTv.setText(uName);
        myHolder.pTimeTv.setText(pTimeFormat);
        myHolder.pTitleTv.setText(pTitle);
        myHolder.pAbstractionTv.setText(pAbstraction);


        //set user profile picture
        try{

            Picasso.get().load(uDp).placeholder(R.drawable.user_profile).into(myHolder.uPictureIv);

        }catch (Exception e){

        }



        //set post picture

        //if there is no picture in the post
        if (pImage.equals("noImage")){
            //hide imageview
            myHolder.pImageIv.setVisibility(View.GONE);
        }
        try{

            Picasso.get().load(pImage).into(myHolder.pImageIv);

        }catch (Exception e){

        }

        //handle button clicks
        myHolder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //will implement later
                Toast.makeText(context, "More", Toast.LENGTH_SHORT).show();
            }
        });

        myHolder.admireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //will implement later
                Toast.makeText(context, "admire", Toast.LENGTH_SHORT).show();
            }
        });

        myHolder.feedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //will implement later
                Toast.makeText(context, "feedback", Toast.LENGTH_SHORT).show();
            }
        });

        myHolder.bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //will implement later
                Toast.makeText(context, "bookmark", Toast.LENGTH_SHORT).show();
            }
        });




    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{

        //views from research_post_layout.xml
        ImageView uPictureIv, pImageIv;
        TextView uNameTv, pTimeTv, pTitleTv,pAbstractionTv,pLikesTv;
        ImageButton moreButton;
        Button admireButton, feedbackButton, bookmarkBtn;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            uPictureIv=itemView.findViewById(R.id.homeProfileImage_id);
            pImageIv=itemView.findViewById(R.id.researchPostImage_id);
            uNameTv=itemView.findViewById(R.id.homeResearcherName_id);
            pTimeTv=itemView.findViewById(R.id.homePostTime_id);
            pTitleTv=itemView.findViewById(R.id.homePostTitleText_id);
            pAbstractionTv=itemView.findViewById(R.id.homePostDescText_id);
            pLikesTv=itemView.findViewById(R.id.likeCounterText_id);
            moreButton=itemView.findViewById(R.id.moreButton_id);
            admireButton=itemView.findViewById(R.id.admire_btn_id);
            feedbackButton=itemView.findViewById(R.id.feedback_btn_id);
            bookmarkBtn=itemView.findViewById(R.id.bookmark_btn_id);
        }
    }
}
