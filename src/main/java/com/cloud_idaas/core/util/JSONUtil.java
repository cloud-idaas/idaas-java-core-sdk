package com.cloud_idaas.core.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class JSONUtil {

    private static final Gson GSON_INSTANCE = new GsonBuilder()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC)
            .disableHtmlEscaping()
            .disableJdkUnsafe()
            .create();

    public static String toJSONString(Object o) {
        return GSON_INSTANCE.toJson(o);
    }

    public static byte[] toJSONBytes(Object o) {
        String s = toJSONString(o);
        return s.getBytes(StandardCharsets.UTF_8);
    }

    public static <T> T parseObject(String jsonString, Class<T> clazz) throws JsonSyntaxException {
        return GSON_INSTANCE.fromJson(jsonString, clazz);
    }

    public static <T> T parseObject(String jsonString, Type type) throws JsonSyntaxException {
        return GSON_INSTANCE.fromJson(jsonString, type);
    }

    public static <T> List<T> parseArray(String jsonString, Class<T> clazz) throws JsonSyntaxException {
        return GSON_INSTANCE.fromJson(jsonString, TypeToken.getParameterized(List.class, clazz).getType());
    }

    public static <K, V> Map<K, V> parseMap(String jsonString, Class<K> kClazz, Class<V> vClazz) throws JsonSyntaxException {
        return GSON_INSTANCE.fromJson(jsonString, TypeToken.getParameterized(Map.class, kClazz, vClazz).getType());
    }
}
