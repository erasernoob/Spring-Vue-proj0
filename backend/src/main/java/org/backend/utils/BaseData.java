package org.backend.utils;

import org.springframework.validation.ObjectError;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

/**
 * 利用反射机制，将后端需要与前端交互的对象进行复制属性。
 */
public interface BaseData {

    default public <V> V asViewObject(Class<V> clazz, Consumer<V> consumer) {
        V v = this.asViewObject(clazz);
        consumer.accept(v);
        return v;
    }

    default <V> V asViewObject(Class<V> clazz) {
        try {
            Field[] fields = clazz.getDeclaredFields();
            Constructor<V> constructors = clazz.getConstructor();
            V v = constructors.newInstance();
            for (Field field : fields) {convert(field, v);}
            return v;
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException |
                NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void convert(Field field, Object v) {
        try {
            Field source = this.getClass().getDeclaredField(field.getName());
            source.setAccessible(true);
            field.setAccessible(true);
            field.set(v, source.get(this));
        } catch (IllegalAccessException | NoSuchFieldException ignored) {} // 找到了没有存在的field直接忽略

    }
}
