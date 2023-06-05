package com.cuttlesystems.util;

import android.graphics.Bitmap;

import java.util.ArrayList;

public interface SelectedListenerAddress {
    void onMyItemClick(String address);
    void copyQr(Bitmap qr);
}