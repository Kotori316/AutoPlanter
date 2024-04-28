package com.kotori316.auto_planter;

public final class MixinHelper {
    public static <T> T cast(Object o, Class<T> clazz) {
        return clazz.cast(o);
    }
}
