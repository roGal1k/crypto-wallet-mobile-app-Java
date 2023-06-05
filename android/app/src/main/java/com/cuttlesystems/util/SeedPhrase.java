package com.cuttlesystems.util;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class SeedPhrase implements Parcelable {
    private String seed;
    private String id;

    public SeedPhrase(String seed, String id) {
        this.seed = seed;
        this.id = id;
    }

    protected SeedPhrase(Parcel in) {
        seed = in.readString();
        id = in.readString();
    }

    public static final Creator<SeedPhrase> CREATOR = new Creator<SeedPhrase>() {
        @Override
        public SeedPhrase createFromParcel(Parcel in) {
            return new SeedPhrase(in);
        }

        @Override
        public SeedPhrase[] newArray(int size) {
            return new SeedPhrase[size];
        }
    };

    public String getSeed()
    {
        return seed;
    }

    public String getId()
    {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(seed);
        dest.writeString(id);
    }
}
