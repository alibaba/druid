package com.alibaba.druid.support.spring.stat;

import java.lang.reflect.Method;

public class SpringMethodInfo {

    private String   signature;

    private Class<?> instanceClass;
    private Method   method;

    public SpringMethodInfo(Class<?> instanceClass, Method method){
        this.instanceClass = instanceClass;
        this.method = method;
    }

    public String getClassName() {
        return instanceClass.getName();
    }

    public String getMethodName() {
        return method.getName();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + instanceClass.getName().hashCode();
        result = prime * result + method.getName().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        SpringMethodInfo other = (SpringMethodInfo) obj;

        if (!instanceClass.getName().equals(other.instanceClass.getName())) {
            return false;
        }

        if (!method.getName().equals(other.method.getName())) {
            return false;
        }
        
        if (method.getParameterTypes().length != other.method.getParameterTypes().length) {
            return false;
        }
        
        for (int i = 0; i < method.getParameterTypes().length; ++i) {
            if (method.getParameterTypes()[i].getName().equals(other.method.getParameterTypes()[i].getName())) {
                return false;
            }
        }

        return true;
    }

    public static String getMethodSignature(Method method) {
        StringBuffer sb = new StringBuffer();

        sb.append(method.getName() + "(");
        Class<?>[] params = method.getParameterTypes();
        for (int j = 0; j < params.length; j++) {
            sb.append(params[j].getName());
            if (j < (params.length - 1)) sb.append(",");
        }
        sb.append(")");

        return sb.toString();
    }

    public String getSignature() {
        if (signature == null) {
            signature = getMethodSignature(method);
        }
        return signature;
    }
}
