package com.cuttlesystems.util;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

public class TotalBalance implements Parcelable {

// -------------------------------------------------------------------------------------------------
    private static final String KZT = " KZT";
    private static final String BTC = " BTC";
    private static final String USD = " USD";
// -------------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------

    private String totalBTC = "0.0000"+BTC;
    private String totalUSD = "0.0000"+USD;
    private String totalKZT = "0.0000"+KZT;

    TotalBalance( String btc, String usd, String kzt){
        totalBTC = btc+BTC;
        totalUSD = usd+USD;
        totalKZT = kzt+KZT;
    }

    protected TotalBalance(Parcel in) {
        totalBTC = in.readString();
        totalUSD = in.readString();
        totalKZT = in.readString();
    }

    public static final Creator<TotalBalance> CREATOR = new Creator<TotalBalance>() {
        @Override
        public TotalBalance createFromParcel(Parcel in) {
            return new TotalBalance(in);
        }

        @Override
        public TotalBalance[] newArray(int size) {
            return new TotalBalance[size];
        }
    };

    public String getBalanceUSD()
    {
        return totalUSD;
    }
    public String getBalanceBTC()
    {
        return totalBTC;
    }
    public String getBalanceKZT()
    {
        return totalKZT;
    }

    public void setBalanceUSD(String usd)
    {
        totalUSD = usd+USD;
    }
    public void setBalanceBTC(String btc)
    {
        totalBTC = btc+BTC;
    }
    public void setBalanceKZT(String kzt)
    {
        totalKZT = kzt+KZT;
    }

    public ArrayList<String> getAllTotal()
    {
        return new ArrayList<>(Arrays.asList(totalBTC, totalKZT, totalUSD));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(totalBTC);
        dest.writeString(totalUSD);
        dest.writeString(totalKZT);
    }
}
