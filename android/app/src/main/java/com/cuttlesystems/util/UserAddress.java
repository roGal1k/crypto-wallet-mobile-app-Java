package com.cuttlesystems.util;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class UserAddress implements Parcelable {
    private String address;
    //private String id;
    private String balance;

    public UserAddress(String address, String balance){ //String id) {
        this.address = address;
        this.balance = balance;
        //this.id = id;
    }

    protected UserAddress(Parcel in) {
        address = in.readString();
        balance = in.readString();
        //id = in.readString();
    }

    public static final Creator<UserAddress> CREATOR = new Creator<UserAddress>() {
        @Override
        public UserAddress createFromParcel(Parcel in) {
            return new UserAddress(in);
        }

        @Override
        public UserAddress[] newArray(int size) {
            return new UserAddress[size];
        }
    };

    public String getAddress() {
        return address;
    }

    public String getBalance() {
        return balance;
    }

    //public String getId() {
    //    return id;
    //}

    public void setAddress(String address) {
        this.address = address;
    }

    //public void setId(String id) {
    //    this.id = id;
    //}

    public void setBalance(String balance) {
        this.balance = balance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(balance);
        //dest.writeString(id);
    }
}
