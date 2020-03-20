package com.shawn.researjour.Models;

public class ModelClassPost {

    //use same name as we given while uploading post
    String uName,uid,uEmail,postid,pLikes,pComments,uDp,pTime,postimage,title,abstraction,videoLink;

    public ModelClassPost() {
    }

    public ModelClassPost(String uName, String uid, String uEmail,String postid,String pLikes,String pComments,String uDp, String pTime, String postimage, String title, String abstraction,String videoLink) {
        this.uName = uName;
        this.uDp=uDp;
        this.uid = uid;
        this.uEmail = uEmail;
        this.postid=postid;
        this.pLikes=pLikes;
        this.pComments=pComments;
        this.pTime = pTime;
        this.postimage = postimage;
        this.title = title;
        this.abstraction = abstraction;
        this.videoLink = videoLink;

    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getpLikes() {
        return pLikes;
    }

    public void setpLikes(String pLikes) {
        this.pLikes = pLikes;
    }

    public String getpComments() {
        return pComments;
    }

    public void setpComments(String pComments) {
        this.pComments = pComments;
    }

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbstraction() {
        return abstraction;
    }

    public void setAbstraction(String abstraction) {
        this.abstraction = abstraction;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }
}