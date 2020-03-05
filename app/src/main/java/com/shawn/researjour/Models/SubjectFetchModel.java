package com.shawn.researjour.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class SubjectFetchModel implements Parcelable {
    public boolean isSelected;
    public String sub;
    public SubjectFetchModel(){

    }

    protected SubjectFetchModel(Parcel in) {
        isSelected = in.readByte() != 0;
        sub = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeString(sub);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SubjectFetchModel> CREATOR = new Creator<SubjectFetchModel>() {
        @Override
        public SubjectFetchModel createFromParcel(Parcel in) {
            return new SubjectFetchModel(in);
        }

        @Override
        public SubjectFetchModel[] newArray(int size) {
            return new SubjectFetchModel[size];
        }
    };

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
