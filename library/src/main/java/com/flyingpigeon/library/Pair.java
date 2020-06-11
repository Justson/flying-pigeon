package com.flyingpigeon.library;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author ringle-android
 * @date 20-6-10
 * @since 1.0.0
 */
public abstract class Pair implements Parcelable {
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

        public boolean isValue() {
            return value;
        }

        public void setValue(boolean value) {
            this.value = value;
        }
    }


}



