package aie.amg.theshow.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;

public class JsonToModel<T> {
    private JsonType type;
    private JSONArray array;
    private JSONObject object;
    private Class clazz;


    public JsonToModel(JSONObject object, Class<T> clazz) {
        this.object = object;
        this.clazz = clazz;
        type = JsonType.OBJECT;
    }

    public JsonToModel(String data, Class<T> clazz) throws JSONException {
        checkJSON(data);
        this.clazz = clazz;

    }

    private void checkJSON(String data) throws JSONException {
        String tmp = data.trim();
        if (tmp.startsWith("[")) {
            type = JsonType.Array;
            array = new JSONArray(data);
        } else if (tmp.startsWith("{")) {
            type = JsonType.OBJECT;
            object = new JSONObject();
        } else
            type = JsonType.Error;
    }


    public ArrayList<T> getArray() throws JSONException {
        if (type == JsonType.Error)
            return null;

        ArrayList<T> items = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Object o = array.get(i);
            if (o instanceof JSONObject) {

                object = (JSONObject) o;
                T item = getObject();
                items.add(item);
            }
        }

        return items;
    }


    public <S> T getObject() {
        if (type == JsonType.Error) return null;
        try {

            T t = (T) clazz.newInstance();
            for (Iterator<String> it = object.keys(); it.hasNext(); ) {
                String key = it.next();
                Object o = object.get(key);
                String key2 = key.substring(0, 1).toUpperCase() + key.substring(1);
                if (o instanceof JSONArray) {

                    try {
                        Class<S> c = getChildArrayClass(key);

                        if (c != null) {
                            ArrayList<S> list = new JsonToModel<>(o.toString(), c).getArray();
                            Method method = clazz.getMethod("set" + key2, list.getClass());
                            method.invoke(t, list);
                        }
                    } catch (Exception e) {
                    }
                } else {
                    try {

                        Method method = clazz.getMethod("set" + key2, convertClass(o.getClass()));
                        method.invoke(t, o);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return t;

        } catch (Exception e) {

        }
        return null;
    }

    private <S> Class<S> getChildArrayClass(String key) {

        Field field = null;
        for (Class c = clazz; c != null; c = clazz.getSuperclass()) {
            try {
                field = c.getDeclaredField(key);
                break;
            } catch (NoSuchFieldException ignored) {
            }
        }

        if (field == null) return null;
        if (Modifier.isPrivate(field.getModifiers()) || Modifier.isProtected(field.getModifiers()))
            field.setAccessible(true);

        try {

            JsonArray array = field.getAnnotation(JsonArray.class);
            return (Class<S>) array.clazz();
        } catch (Exception e) {
            throw new RuntimeException("you must declare JsonArray annotation above arrayList", e);
        }

    }

    private Class convertClass(Class c) {
        if (c == Integer.class) {
            return int.class;
        } else if (c == Double.class) {
            return double.class;
        }
        return c;
    }

    public JsonType getType() {
        return type;
    }

    public enum JsonType {
        OBJECT,
        Array,
        Error
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface JsonArray {
        Class clazz();
    }
}
