package com.tencent.community.util;


import com.alibaba.fastjson.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class JsonUtils {


    /*
        指定Map value参数为 map 参数形式Map<String, ?>
     */

//    public static String getJsonString(int code, String message, Map<String, ?> map){
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("code", code);
//        jsonObject.put("message", message);
//        if(map != null) {
//            jsonObject.putAll(map);
//        }
//        return jsonObject.toJSONString();
//    }

    public static String getJsonString(int code, String message, Map<String, Object> map){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("message", message);
        if(map != null){
            for(String key : map.keySet()){
                jsonObject.put(key, map.get(key));
            }
        }
        return jsonObject.toJSONString();
    }

    /*
        进行函数重载，方便不同参数传入获取
     */

    public static String getJsonString(int code, String message){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("message", message);
        return jsonObject.toJSONString();
    }

    public static String getJsonString(int code){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        return jsonObject.toJSONString();
    }

    public static void main(String[] args) {
        Map<String, Map<String, Object>> maps = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        map.put("data", "13r13t1");
        maps.put("all", map);
//        String res = getJsonString(200, "成功", maps);
//        System.out.println(res);
    }
}
