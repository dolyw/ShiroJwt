package com.wang.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * TODO：Json和对象List的互相转换，转List必须Json最外层加[]，转Object，Json最外层不要加[]
 * @author Wang926454
 * @date 2018/8/9 15:37
 */
public class JsonListUtil {
    /**
     * JSON 转 POJO
     */
    public static <T> T jsonToObject(String pojo, Class<T> clazz) {
        return JSONObject.parseObject(pojo, clazz);
    }

    /**
     * POJO 转 JSON
     */
    public static <T> String objectToJson(T t){
        return JSONObject.toJSONString(t);
    }

    /**
     * JSON 转 List<T>
     */
    public static <T> List<T> jsonToList(String jsonString, Class<T> clazz) {
        return (List<T>) JSONArray.parseArray(jsonString, clazz);
    }

    /**
     * List<T> 转 JSON
     */
    public static <T> String listToJson(List<T> t) {
        return JSON.toJSONString(t);
    }
}
