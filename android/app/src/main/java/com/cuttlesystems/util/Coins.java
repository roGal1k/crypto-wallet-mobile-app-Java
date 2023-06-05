package com.cuttlesystems.util;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Coins implements Parcelable {
    private String id;
    private String icon;
    private String fullName;
    private String shortName;
    private String state;

    public Coins(String id, String full_name, String short_name, String icon,String state) {
        this.id = id;
        this.fullName = full_name;
        this.shortName = short_name;
        this.icon = icon;
        this.state = state;
    }

    protected Coins(Parcel in) {
        id = in.readString();
        icon = in.readString();
        fullName = in.readString();
        shortName = in.readString();
        state = in.readString();
    }

    public static final Creator<Coins> CREATOR = new Creator<Coins>() {
        @Override
        public Coins createFromParcel(Parcel in) {
            return new Coins(in);
        }

        @Override
        public Coins[] newArray(int size) {
            return new Coins[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getShortName(){
        return shortName;
    }

    public String getIcon() {
        return icon;
    }

    public String getState(){
        return state;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setShortName(String shortName){
        this.shortName = shortName;
    }

    public void setIcon(String icon){
        this.icon = icon;
    }

    public void setState(String state){
        this.state = state;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(icon);
        dest.writeString(fullName);
        dest.writeString(shortName);
        dest.writeString(state);
    }
}
