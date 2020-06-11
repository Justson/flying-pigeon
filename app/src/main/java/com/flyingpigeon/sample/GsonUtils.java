package com.flyingpigeon.sample;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GsonUtils {

    private GsonUtils() {
        throw new IllegalStateException("not allow init this constructor ");
    }

    private static TypeAdapter<Boolean> booleanTypeAdapter = new TypeAdapter<Boolean>() {
        @Override
        public void write(JsonWriter out, Boolean value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value);
            }
        }

        @Override
        public Boolean read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            switch (peek) {
                case BOOLEAN:
                    return in.nextBoolean();
                case NULL:
                    in.nextNull();
                    return null;
                case NUMBER:
                    return in.nextInt() != 0;
                case STRING:
                    return Boolean.parseBoolean(in.nextString());
                default:
                    throw new IllegalStateException("Expected BOOLEAN or NUMBER but was " + peek);
            }
        }
    };

    private static final Gson GSON = new GsonBuilder().serializeNulls()
            .registerTypeAdapter(Integer.class, new TypeAdapter<Integer>() {
                @Override
                public void write(JsonWriter jsonWriter, Integer integer) throws IOException {
                    if (integer == null) {
                        jsonWriter.value(0);
                    } else {
                        jsonWriter.value(integer);
                    }
                }

                @Override
                public Integer read(JsonReader jsonReader) throws IOException {
                    try {
                        return Integer.parseInt(jsonReader.nextString());
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                }
            })
            .registerTypeAdapter(boolean.class, booleanTypeAdapter)
            .registerTypeAdapter(Boolean.class, booleanTypeAdapter)
            .registerTypeAdapter(Long.class, new TypeAdapter<Long>() {
                @Override
                public void write(JsonWriter jsonWriter, Long aLong) throws IOException {
                    if (aLong == null) {
                        jsonWriter.value(0);
                    } else {
                        jsonWriter.value(aLong);
                    }
                }

                @Override
                public Long read(JsonReader jsonReader) throws IOException {
                    try {
                        return Long.parseLong(jsonReader.nextString());
                    } catch (NumberFormatException e) {
                        return 0L;
                    }
                }
            })

            .setLenient()
            .excludeFieldsWithModifiers(Modifier.STATIC, Modifier.FINAL)
            .create();

    /**
     * .registerTypeAdapter(ECMessage.class, new TypeAdapter<ECMessage>() {
     *
     * @return
     * @Override public void write(JsonWriter out, ECMessage value) throws IOException {
     * <p>
     * }
     * @Override public ECMessage read(JsonReader in) throws IOException {
     * return null;
     * }
     * })
     */
    public static Gson getGson() {
        return GSON;
    }

    public static <T> List<T> parse2List(String json, Class<T[]> clazz) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        T[] arr = getGson().fromJson(json, clazz);
        // 不能直接返回Arrays.asList(arr)
        List<T> result = new ArrayList<>();
        result.addAll(Arrays.asList(arr));
        return result;
    }

    public static <T> ArrayList<T> parse2ArrayList(String json, Class<T[]> clazz) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        T[] arr = getGson().fromJson(json, clazz);
        // 不能直接返回Arrays.asList(arr)
        ArrayList<T> result = new ArrayList<>();
        result.addAll(Arrays.asList(arr));
        return result;
    }

    public static <T> T parse2Obj(String json, Type type) {
        return getGson().fromJson(json, type);
    }

    public static <T> T parse2Obj(String json, Class<T> tClass) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        return getGson().fromJson(json, tClass);
    }

    public static <T> T parse2ObjQuiet(String json, Class<T> tClass) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            return getGson().fromJson(json, tClass);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    public static String toJson(Object object) {
        if (object == null) {
            return "";
        }
        return getGson().toJson(object);
    }

    public static String toJson(JsonElement object) {
        if (object == null) {
            return "";
        }
        return getGson().toJson(object);
    }

    public static String getString(String json, String key) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.optString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getInt(String json, String key) {
        if (TextUtils.isEmpty(json)) {
            return 0;
        }
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.optInt(key, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
