package com.flyingpigeon.sample;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author ringle-android
 * @date 20-6-11
 * @since 1.0.0
 */
public class Information implements Parcelable {

    private String name;
    private String id;
    private int weight;
    private short aShort;
    private char aChar;
    private float height;
    private byte mByte;
    private double aDouble;
    private long aLong;

    public Information(String name, String id, int weight, short aShort, char aChar, float height, byte aByte, double aDouble, long aLong) {
        this.name = name;
        this.id = id;
        this.weight = weight;
        this.aShort = aShort;
        this.aChar = aChar;
        this.height = height;
        mByte = aByte;
        this.aDouble = aDouble;
        this.aLong = aLong;
    }


    protected Information(Parcel in) {
        name = in.readString();
        id = in.readString();
        weight = in.readInt();
        aShort = (short) in.readInt();
        aChar = (char) in.readInt();
        height = in.readFloat();
        mByte = in.readByte();
        aDouble = in.readDouble();
        aLong = in.readLong();
    }

    public static final Creator<Information> CREATOR = new Creator<Information>() {
        @Override
        public Information createFromParcel(Parcel in) {
            return new Information(in);
        }

        @Override
        public Information[] newArray(int size) {
            return new Information[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeInt(weight);
        dest.writeInt((int) aShort);
        dest.writeInt((int) aChar);
        dest.writeFloat(height);
        dest.writeByte(mByte);
        dest.writeDouble(aDouble);
        dest.writeLong(aLong);
    }
}
