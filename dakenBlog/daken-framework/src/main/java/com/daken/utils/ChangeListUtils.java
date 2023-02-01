package com.daken.utils;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChangeListUtils {
    /**
     * Object对象转 List集合
     *
     * @param object Object对象
     * @param clazz 需要转换的集合
     * @param <T> 泛型类
     * @return
     */
    public static <T> List<T> changeList(Object object, Class<T> clazz){
        try {
            List<T> result = new ArrayList<>();
            if (object instanceof List<?>){
                for (Object o : (List<?>) object) {
                    String string = JSONObject.toJSONString(o);
                    T t = JSONObject.parseObject(string, clazz);
                    result.add(t);
                }
                return result;
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
