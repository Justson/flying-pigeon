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
package com.flyingpigeon.library.ashmem;

import android.annotation.SuppressLint;
import android.os.MemoryFile;
import android.os.ParcelFileDescriptor;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author cenxiaozhong
 * @date 2020/6/26
 * @since 1.0.0
 */
public class Ashmem {

    public static ParcelFileDescriptor byteArrayToFileDescriptor(byte[] array) {
        /**
         * Landroid/os/MemoryFile;->getFileDescriptor()Ljava/io/FileDescriptor;	greylist
         * 灰名单接口
         * Android 10.0发布后，非SDK接口的划分变化
         *
         * greylist 无限制，可以正常使用。
         */
        try {
            MemoryFile memoryFile = new MemoryFile(UUID.randomUUID().toString(), array.length);
            memoryFile.writeBytes(array, 0, 0, array.length);
            @SuppressLint("DiscouragedPrivateApi")
            Method method = MemoryFile.class.getDeclaredMethod("getFileDescriptor");
            FileDescriptor fd = (FileDescriptor) method.invoke(memoryFile);
            return ParcelFileDescriptor.dup(fd);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
