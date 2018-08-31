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
    public static <T> T jsonToObject(String pojo, Class<T> tclass) {
        try {
            return JSONObject.parseObject(pojo, tclass);
        } catch (Exception e) {
            System.out.println(tclass + "转JSON失败");
        }
        return null;
    }

    /**
     * POJO 转 JSON
     */
    public static <T> String objectToJson(T tResponse){
        String pojo = JSONObject.toJSONString(tResponse);
        return pojo;
    }

    /**
     * List<T> 转 json
     */
    public static <T> String listToJson(List<T> ts) {
        String jsons = JSON.toJSONString(ts);
        return jsons;
    }

    /**
     * json 转 List<T>
     */
    public static <T> List<T> jsonToList(String jsonString, Class<T> clazz) {
        @SuppressWarnings("unchecked")
        List<T> ts = (List<T>) JSONArray.parseArray(jsonString, clazz);
        return ts;
    }
}
