package com.cuttlesystems.util;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class UserBalance implements Parcelable {
    private String id;
    private String total;
    private String name;
    private String alphabeticCode;

    private String icon;

    public UserBalance(String id, String total, String name, String alphabeticCode, String icon) {
        this.id = id;
        this.total = total;
        this.name = name;
        this.alphabeticCode = alphabeticCode;
        this.icon = icon;
    }

    protected UserBalance(Parcel in) {
        id = in.readString();
        total = in.readString();
        name = in.readString();
        alphabeticCode = in.readString();
        icon = in.readString();
    }

    public static final Creator<UserBalance> CREATOR = new Creator<UserBalance>() {
        @Override
        public UserBalance createFromParcel(Parcel in) {
            return new UserBalance(in);
        }

        @Override
        public UserBalance[] newArray(int size) {
            return new UserBalance[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAlphabeticCode(){
        return alphabeticCode;
    }

    public String getBalance() {
        return total;
    }

    public String getIcon() {return this.icon; }
    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setAlphabeticCode(String alphabeticCode){
        this.alphabeticCode = alphabeticCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(icon);
        dest.writeString(total);
        dest.writeString(name);
        dest.writeString(alphabeticCode);
    }
}
