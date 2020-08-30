package com.jay.nearbysample.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public final class BitmapUtils {
    private BitmapUtils(){}

    /**
     * Converts bitmap to byte array in PNG format
     * @param bitmap source bitmap
     * @return result byte array
     */
    public static byte[] convertBitmapToByteArray(Bitmap bitmap){
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return baos.toByteArray();
        }finally {
            if(baos != null){
                try {
                    baos.close();
                } catch (IOException e) {
                    Log.e(BitmapUtils.class.getSimpleName(), "ByteArrayOutputStream was not closed");
                }
            }
        }
    }

    /**
     * Converts bitmap to the byte array without compression
     * @param bitmap source bitmap
     * @return result byte array
     */
    public static byte[] convertBitmapToByteArrayUncompressed(Bitmap bitmap){
        ByteBuffer byteBuffer = ByteBuffer.allocate(bitmap.getByteCount());
        bitmap.copyPixelsToBuffer(byteBuffer);
        byteBuffer.rewind();
        return byteBuffer.array();
    }

    /**
     * Converts compressed byte array to bitmap
     * @param src source array
     * @return result bitmap
     */
    public static Bitmap convertCompressedByteArrayToBitmap(byte[] src){
        return BitmapFactory.decodeByteArray(src, 0, src.length);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }

}
