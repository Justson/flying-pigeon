package com.flyingpigeon.library.serialization;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

/**
 * @author cenxiaozhong
 * @date 2020/6/26
 * @since 1.0.0
 */
public class ParcelableUtils {

    private ParcelableUtils() {
    }

    public static byte[] parcelable2ByteArray(Parcelable parcelable) {
        Parcel p = Parcel.obtain();
        parcelable.writeToParcel(p, 0);
        byte[] bytes = p.marshall();
        p.recycle();
        return bytes;
    }

    public static String parcelable2String(Parcelable parcelable) {
        return Base64.encodeToString(parcelable2ByteArray(parcelable), Base64.DEFAULT);
    }

    public static <T> T byteArray2Parcelable(byte[] bytes, Parcelable.Creator<T> creator) {
        Parcel parcel = unmarshall(bytes);
        return creator.createFromParcel(parcel);
    }

    public static <T> T string2Parcelable(String str, Parcelable.Creator<T> creator) {
        byte[] bytes = Base64.decode(str, Base64.DEFAULT);
        return byteArray2Parcelable(bytes, creator);
    }

    private static Parcel unmarshall(byte[] bytes) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);
        return parcel;
    }
}
