package com.shawn.researjour.Models;

public class ModelClassPost {

    //use same name as we given while uploading post
    String uName,uid,uEmail,uDp,pTime,postimage,title,abstraction;

    public ModelClassPost() {

    }

    public ModelClassPost(String uName, String uid, String uEmail, String pTime, String postimage, String title, String abstraction) {
        this.uName = uName;
        this.uid = uid;
        this.uEmail = uEmail;
        this.pTime = pTime;
        this.postimage = postimage;
        this.title = title;
        this.abstraction = abstraction;
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

    public String getuDp() {
        return uEmail;
    }

    public void setuDp(String uEmail) {
        this.uEmail = uEmail;
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
}
