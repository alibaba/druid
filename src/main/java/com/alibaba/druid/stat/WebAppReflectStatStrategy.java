package com.alibaba.druid.stat;

import java.lang.reflect.Method;
import java.util.HashMap;

import com.alibaba.druid.util.ReflectionUtils;

public class WebAppReflectStatStrategy extends ReflectStatStrategy {

    public Object invoke(Object target, String methodName, Integer id) {
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
