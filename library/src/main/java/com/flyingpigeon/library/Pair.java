package com.flyingpigeon.library;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;

import static com.flyingpigeon.library.Config.PREFIX;

/**
 * @author xiaozhongcen
 * @date 20-6-10
 * @since 1.0.0
 */
public abstract class Pair implements Parcelable {
    private static final String TAG = PREFIX + Pair.class.getSimpleName();
    private String key;

    protected Pair(String key) {
        this.key = key;
    }

    protected Pair(Parcel in) {
        key = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public static class PairInt extends Pair {
        private int value;

        public PairInt(String key, int value) {
            super(key);
            this.value = value;
        }

        protected PairInt(Parcel in) {
            super(in);
            value = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(value);
        }

        public static final Creator<PairInt> CREATOR = new Creator<PairInt>() {
            @Override
            public PairInt createFromParcel(Parcel in) {
                return new PairInt(in);
            }

            @Override
            public PairInt[] newArray(int size) {
                return new PairInt[size];
            }
        };

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }


    public static class PairDouble extends Pair {
        private double value;

        public PairDouble(String key, double value) {
            super(key);
            this.value = value;
        }

        protected PairDouble(Parcel in) {
            super(in);
            value = in.readDouble();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeDouble(value);
        }

        public static final Creator<PairDouble> CREATOR = new Creator<PairDouble>() {
            @Override
            public PairDouble createFromParcel(Parcel in) {
                return new PairDouble(in);
            }

            @Override
            public PairDouble[] newArray(int size) {
                return new PairDouble[size];
            }
        };

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }
    }


    public static class PairLong extends Pair {
        private long value;

        public PairLong(String key, long value) {
            super(key);
            this.value = value;
        }

        protected PairLong(Parcel in) {
            super(in);
            value = in.readLong();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeLong(value);
        }

        public static final Creator<PairLong> CREATOR = new Creator<PairLong>() {
            @Override
            public PairLong createFromParcel(Parcel in) {
                return new PairLong(in);
            }

            @Override
            public PairLong[] newArray(int size) {
                return new PairLong[size];
            }
        };

        public long getValue() {
            return value;
        }

        public void setValue(long value) {
            this.value = value;
        }
    }


    public static class PairShort extends Pair {
        private short value;

        public PairShort(String key, short value) {
            super(key);
            this.value = value;
        }

        protected PairShort(Parcel in) {
            super(in);
            value = (short) in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(value);
        }

        public static final Creator<PairShort> CREATOR = new Creator<PairShort>() {
            @Override
            public PairShort createFromParcel(Parcel in) {
                return new PairShort(in);
            }

            @Override
            public PairShort[] newArray(int size) {
                return new PairShort[size];
            }
        };

        public short getValue() {
            return value;
        }

        public void setValue(short value) {
            this.value = value;
        }
    }


    public static class PairFloat extends Pair {
        private float value;

        public PairFloat(String key, float value) {
            super(key);
            this.value = value;
        }

        protected PairFloat(Parcel in) {
            super(in);
            value = in.readFloat();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeFloat(value);
        }

        public static final Creator<PairFloat> CREATOR = new Creator<PairFloat>() {
            @Override
            public PairFloat createFromParcel(Parcel in) {
                return new PairFloat(in);
            }

            @Override
            public PairFloat[] newArray(int size) {
                return new PairFloat[size];
            }
        };

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }
    }


    public static class PairByte extends Pair {
        private byte value;

        public PairByte(String key, byte value) {
            super(key);
            this.value = value;
        }

        protected PairByte(Parcel in) {
            super(in);
            value = in.readByte();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeByte(value);
        }

        public static final Creator<PairByte> CREATOR = new Creator<PairByte>() {
            @Override
            public PairByte createFromParcel(Parcel in) {
                return new PairByte(in);
            }

            @Override
            public PairByte[] newArray(int size) {
                return new PairByte[size];
            }
        };

        public byte getValue() {
            return value;
        }

        public void setValue(byte value) {
            this.value = value;
        }
    }


    public static class PairBoolean extends Pair {
        private boolean value;

        public PairBoolean(String key, boolean value) {
            super(key);
            this.value = value;
        }

        protected PairBoolean(Parcel in) {
            super(in);
            value = in.readByte() == 1;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeByte((byte) (value ? 1 : 0));
        }

        public static final Creator<PairBoolean> CREATOR = new Creator<PairBoolean>() {
            @Override
            public PairBoolean createFromParcel(Parcel in) {
                return new PairBoolean(in);
            }

            @Override
            public PairBoolean[] newArray(int size) {
                return new PairBoolean[size];
            }
        };

        public boolean isValue() {
            return value;
        }

        public void setValue(boolean value) {
            this.value = value;
        }
    }

    public static class PairParcelable extends Pair {
        private Parcelable value;

        public PairParcelable(String key, Parcelable value) {
            super(key);
            this.value = value;
            Log.e(TAG, "key:" + this.getKey());
        }

        protected PairParcelable(String key) {
            super(key);

        }

        protected PairParcelable(Parcel in) throws ClassNotFoundException {
            super(in);
            value = in.readParcelable(Class.forName(getKey()).getClassLoader());
            Log.e(TAG, "PairParcelable:" + value);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeParcelable(value, flags);
        }

        public static final Creator<PairParcelable> CREATOR = new Creator<PairParcelable>() {
            @Override
            public PairParcelable createFromParcel(Parcel in) {
                Log.e(TAG, "createFromParcel");
                try {
                    return new PairParcelable(in);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public PairParcelable[] newArray(int size) {
                return new PairParcelable[size];
            }
        };

        public Parcelable getValue() {
            return value;
        }

        public void setValue(Parcelable value) {
            this.value = value;
        }
    }


    public static class PairString extends Pair {
        private String value;

        public PairString(String key, String value) {
            super(key);
            this.value = value;
            Log.e(TAG, "key:" + this.getKey());
        }

        protected PairString(String key) {
            super(key);

        }

        protected PairString(Parcel in) throws ClassNotFoundException {
            super(in);
            value = in.readString();
            Log.e(TAG, "PairParcelable:" + value);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(value);
        }

        public static final Creator<PairString> CREATOR = new Creator<PairString>() {
            @Override
            public PairString createFromParcel(Parcel in) {
                Log.e(TAG, "createFromParcel");
                try {
                    return new PairString(in);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public PairString[] newArray(int size) {
                return new PairString[size];
            }
        };

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }


    public static class PairSerializable extends Pair {
        private Serializable value;

        public PairSerializable(String key, Serializable value) {
            super(key);
            this.value = value;
            Log.e(TAG, "key:" + this.getKey());
        }

        protected PairSerializable(String key) {
            super(key);

        }

        protected PairSerializable(Parcel in) throws ClassNotFoundException {
            super(in);
            value = in.readSerializable();
            Log.e(TAG, "PairParcelable:" + value);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeSerializable(value);
        }

        public static final Creator<PairParcelable> CREATOR = new Creator<PairParcelable>() {
            @Override
            public PairParcelable createFromParcel(Parcel in) {
                Log.e(TAG, "createFromParcel");
                try {
                    return new PairParcelable(in);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public PairParcelable[] newArray(int size) {
                return new PairParcelable[size];
            }
        };

        public Serializable getValue() {
            return value;
        }

        public void setValue(Serializable value) {
            this.value = value;
        }
    }

}



