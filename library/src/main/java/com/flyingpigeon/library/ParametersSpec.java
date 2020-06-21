package com.flyingpigeon.library;

/**
 * @author xiaozhongcen
 * @date 20-6-11
 * @since 1.0.0
 */
public class ParametersSpec {
    public static final int PIGEON_PARAMETER_REQUEST_MASK = 0x0000000C;

    public static final int PIGEON_PARAMETER_RESPONSE_MASK = 0x000000F0;

    public static final int PIGEON_PARAMETER_REQUEST_NORMAL = 0x00000000;

    public static final int PIGEON_PARAMETER_REQUEST_LARGE = 0x00000004;

    public static final int PIGEON_PARAMETER_RESPONSE_NORMAL = 0x00000040;

    public static final int PIGEON_PARAMETER_RESPONSE_LARGE = 0x00000080;


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
}
