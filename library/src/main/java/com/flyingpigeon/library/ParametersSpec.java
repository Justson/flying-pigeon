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
package com.flyingpigeon.library;

/**
 * @author xiaozhongcen
 * @date 20-6-11
 * @since 1.0.0
 */
public class ParametersSpec {

    /**
     * request large block params mask
     */
    static final int PIGEON_PARAMETER_REQUEST_MASK = 0x0000000C;

    static final int PIGEON_PARAMETER_RESPONSE_MASK = 0x000000F0;

    static final int PIGEON_PARAMETER_IS_PARCE_MASK = 0x00000300;

    static final int PIGEON_PARAMETER_IS_PARCE_TRUE = 0x00000100;

    static final int PIGEON_PARAMETER_IS_PARCE_FALSE = 0x00000200;

    static final int PIGEON_PARAMETER_REQUEST_NORMAL = 0x00000000;

    static final int PIGEON_PARAMETER_REQUEST_LARGE = 0x00000004;

    static final int PIGEON_PARAMETER_RESPONSE_NORMAL = 0x00000040;

    static final int PIGEON_PARAMETER_RESPONSE_LARGE = 0x00000080;


    public static int getParcelParameter(int flags) {
        return flags & PIGEON_PARAMETER_IS_PARCE_MASK;
    }

    public static int getRequestParameter(int flags) {
        return flags & PIGEON_PARAMETER_REQUEST_MASK;
    }

    public static int getResponseParameter(int flags) {
        return flags & PIGEON_PARAMETER_RESPONSE_MASK;
    }

    public static boolean isRequestParameterLarge(int flags) {
        return getRequestParameter(flags) == PIGEON_PARAMETER_REQUEST_LARGE;
    }

    public static boolean isResponseParameterLarge(int flags) {
        return getResponseParameter(flags) == PIGEON_PARAMETER_RESPONSE_LARGE;
    }

    private static synchronized int setFlag(int old, int flags, int mask) {
        return old = (old & ~mask) | (flags & mask);
    }

    public static int setRequestLarge(int old) {
        return setFlag(old, PIGEON_PARAMETER_REQUEST_LARGE, PIGEON_PARAMETER_REQUEST_MASK);
    }

    public static int setResponseLarge(int old) {
        return setFlag(old, PIGEON_PARAMETER_RESPONSE_LARGE, PIGEON_PARAMETER_RESPONSE_MASK);
    }


    public static int setRequestNormal(int old) {
        return setFlag(old, PIGEON_PARAMETER_REQUEST_NORMAL, PIGEON_PARAMETER_REQUEST_MASK);
    }

    public static int setResponseNormal(int old) {
        return setFlag(old, PIGEON_PARAMETER_RESPONSE_NORMAL, PIGEON_PARAMETER_RESPONSE_MASK);
    }

    public static int setParamParcel(int old, boolean isParcel) {
        return setFlag(old, PIGEON_PARAMETER_IS_PARCE_MASK, isParcel ? PIGEON_PARAMETER_IS_PARCE_TRUE : PIGEON_PARAMETER_IS_PARCE_FALSE);
    }

    public static boolean isParamParcel(int flags) {
        return getParcelParameter(flags) == PIGEON_PARAMETER_IS_PARCE_TRUE;
    }

}
