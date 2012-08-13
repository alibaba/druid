package com.alibaba.druid.support.spring.stat;

import java.lang.reflect.Method;

public class MethodInfo {

    private String       className;
    private String       methodName;
    private final String signature;

    public MethodInfo(Class<?> instanceClass, Method method){
        this.className = instanceClass.getName();
        this.methodName = method.getName();
        signature = getMethodSignature(method);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        result = prime * result + ((signature == null) ? 0 : signature.hashCode());
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
        if (className == null) {
            if (other.className != null) {
                return false;
            }
        } else if (!className.equals(other.className)) {
            return false;
        }

        if (signature == null) {
            if (other.signature != null) {
                return false;
            }
        } else if (!signature.equals(other.signature)) {
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
