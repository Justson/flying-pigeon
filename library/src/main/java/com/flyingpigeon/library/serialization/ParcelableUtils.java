/*
 * Copyright (C)  Justson(https://github.com/Justson/flying-pigeon)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
