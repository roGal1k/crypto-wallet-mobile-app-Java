package com.cuttlesystems.util;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Storyz implements Parcelable {
    private String name;
    private int img;
    private String info;

    public Storyz(String name, int img, String info) {
        this.name = name;
        this.img = img;
        this.info = info;
    }

    protected Storyz(Parcel in) {
        name = in.readString();
        img = in.readInt();
        info = in.readString();
    }

    public static final Creator<Storyz> CREATOR = new Creator<Storyz>() {
        @Override
        public Storyz createFromParcel(Parcel in) {
            return new Storyz(in);
        }

        @Override
        public Storyz[] newArray(int size) {
            return new Storyz[size];
        }
    };

    public String getName() {
        return name;
    }

    public int getImg() {
        return img;
    }

    public String getInfo(){
        return info;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public void setInfo(String info){
        this.info = info;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(img);
        dest.writeString(info);
    }
}
