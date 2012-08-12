package com.alibaba.druid.stat;

import java.lang.reflect.Method;
import java.util.HashMap;

public abstract class ReflectStatStrategy {

    public final static HashMap<Object, HashMap<String, Method>> methodHM = new HashMap<Object, HashMap<String, Method>>();

    public abstract Object invoke(Object obj, String methodName, Integer id);
}
