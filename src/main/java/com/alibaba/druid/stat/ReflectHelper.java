package com.alibaba.druid.stat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.alibaba.druid.util.ReflectionUtils;

public class ReflectHelper {

    private final static List<Object>                             list     = new ArrayList<Object>();
    private final static HashMap<Object, HashMap<String, Method>> methodHM = new HashMap<Object, HashMap<String, Method>>();

    private ReflectHelper(){
    };

    private static ReflectHelper instance = new ReflectHelper();

    public static ReflectHelper getInstance() {
        return instance;
    }

    public Object invoke(Object obj, String methodName, Integer id) {
        Object target = null;
        int objIndex = list.indexOf(obj);
        if (objIndex != -1) {
            target = list.get(objIndex);
        } else {
            target = obj;
        }
        HashMap<String, Method> h = methodHM.get(target);
        boolean existsMethod = h.containsKey(methodName);
        Method method = null;
        if (existsMethod) {
            method = h.get(methodName);
            return ReflectionUtils.callObjectMethod(target, method, id);
        } else {
            method = ReflectionUtils.getObjectMethod(target, methodName, id);
            final HashMap<String, Method> hm = new HashMap<String, Method>();
            hm.put(methodName, method);
            methodHM.put(target, hm);
            return ReflectionUtils.callObjectMethod(target, method, id);
        }
    }
}
