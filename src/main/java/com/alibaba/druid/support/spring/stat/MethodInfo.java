package com.alibaba.druid.support.spring.stat;

import java.lang.reflect.Method;

public class MethodInfo {

    private final Class<?> instanceClass;
    private final Method   method;
    private final String   signature;

    public MethodInfo(Class<?> instanceClass, Method method){
        super();
        this.instanceClass = instanceClass;
        this.method = method;
        signature = getMethodSignature(this.getMethod());
    }

    public Class<?> getInstanceClass() {
        return instanceClass;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((instanceClass == null) ? 0 : instanceClass.hashCode());
        result = prime * result + ((method == null) ? 0 : method.hashCode());
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

        if (getClass() != obj.getClass()) {
            return false;
        }

        MethodInfo other = (MethodInfo) obj;

        if (instanceClass != other.instanceClass) {
            return false;
        }
        
        if (signature == null) {
            return other.signature == null;
        }

        if (!signature.equals(other.signature)) {
            return false;
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
        return signature;
    }
}
