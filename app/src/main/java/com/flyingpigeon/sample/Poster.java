package com.flyingpigeon.sample;

import java.io.Serializable;

/**
 * @author ringle-android
 * @date 20-6-11
 * @since 1.0.0
 */
public class Poster implements Serializable {

    private String name;
    private String alis;
    private int id;
    private long aLong;
    private short aShort;
    private float aFloat;
    private char aChar;
    private byte aByte;
    private double aDouble;

    public Poster(String name, String alis, int id, long aLong, short aShort, float aFloat, char aChar, byte aByte, double aDouble) {
        this.name = name;
        this.alis = alis;
        this.id = id;
        this.aLong = aLong;
        this.aShort = aShort;
        this.aFloat = aFloat;
        this.aChar = aChar;
        this.aByte = aByte;
        this.aDouble = aDouble;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlis() {
        return alis;
    }

    public void setAlis(String alis) {
        this.alis = alis;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getaLong() {
        return aLong;
    }

    public void setaLong(long aLong) {
        this.aLong = aLong;
    }

    public short getaShort() {
        return aShort;
    }

    public void setaShort(short aShort) {
        this.aShort = aShort;
    }

    public float getaFloat() {
        return aFloat;
    }

    public void setaFloat(float aFloat) {
        this.aFloat = aFloat;
    }

    public char getaChar() {
        return aChar;
    }

    public void setaChar(char aChar) {
        this.aChar = aChar;
    }

    public byte getaByte() {
        return aByte;
    }

    public void setaByte(byte aByte) {
        this.aByte = aByte;
    }

    public double getaDouble() {
        return aDouble;
    }

    public void setaDouble(double aDouble) {
        this.aDouble = aDouble;
    }
}
