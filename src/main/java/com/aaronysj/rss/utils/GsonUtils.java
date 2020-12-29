package com.aaronysj.rss.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GsonUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(GsonUtils.class);
    private static Gson gson = null;

    static {
        if (gson == null) {
            gson = new Gson().newBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        }
    }

    private GsonUtils() {
    }

    /**
     * 转成json
     *
     * @param object
     * @return
     */
    public static String convertToString(Object object) {
        String gsonString = null;
        if (gson != null) {
            gsonString = gson.toJson(object);
        }
        return gsonString;
    }

    /**
     * 转成bean
     * 经常用到
     *
     * @param gsonString
     * @param cls
     * @return
     */
    public static <T> T convertToBean(String gsonString, Class<T> cls) {
        T t = null;
        if (gson != null) {
            try{
                t = gson.fromJson(gsonString, cls);
            }catch (Exception e){
                LOGGER.error("class[GsonUtils]method[convertToBean]", e);
            }
        }
        return t;
    }

    /**
     * 转成list
     * 解决泛型问题
     * 经常用到
     *
     * @param json
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> List<T> jsonToList(String json, Class<T> cls) {
        Gson gson = new Gson();
        List<T> list = new ArrayList<T>();
		if (json == null){
			return list;
		}
        JsonArray array = JsonParser.parseString(json).getAsJsonArray();
        for (final JsonElement elem : array) {
            list.add(gson.fromJson(elem, cls));
        }
        return list;
    }


    /**
     * 转成list中有map的
     *
     * @param gsonString
     * @return
     */
    public static <T> List<Map<String, T>> jsonToListMaps(String gsonString) {
        List<Map<String, T>> list = null;
        if (gson != null) {
            list = gson.fromJson(gsonString,
                    new TypeToken<List<Map<String, T>>>() {
                    }.getType());
        }
        return list;
    }

    /**
     * 转成map的
     *
     * @param gsonString
     * @return
     */
    public static <T> Map<String, T> jsonToMaps(String gsonString) {
        Map<String, T> map = null;
        if (gson != null) {
            map = gson.fromJson(gsonString, new TypeToken<Map<String, T>>() {
            }.getType());
        }
        return map;
    }

    /**
     * mapList对象转自定义对象：先转json，再转object（应该有更好的方式，待优化）
     *
     * @param mapList
     * @param c
     * @return
     */
    public static <T> T convertMapListToObject(Object mapList, Class<T> c){
        return GsonUtils.convertToBean(GsonUtils.convertToString(mapList), c);
    }
}
