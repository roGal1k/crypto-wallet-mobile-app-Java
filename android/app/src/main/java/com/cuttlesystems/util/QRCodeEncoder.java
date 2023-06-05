package com.cuttlesystems.util;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.EnumMap;
import java.util.Map;

public class QRCodeEncoder {

// -------------------------------------------------------------------------------------------------
    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF1C1A18;
// -------------------------------------------------------------------------------------------------
    private final String contents;
    private final int dimension;
// -------------------------------------------------------------------------------------------------
    public QRCodeEncoder(String contents, int dimension) {
        this.contents = contents;
        this.dimension = dimension;
    }

    //todo:: Not optimal encode QR
    public Bitmap encodeAsBitmap() throws WriterException {
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1); // default value
        Writer writer = new QRCodeWriter();
        BitMatrix result = writer.encode(contents, BarcodeFormat.QR_CODE, dimension, dimension, hints);
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

}
