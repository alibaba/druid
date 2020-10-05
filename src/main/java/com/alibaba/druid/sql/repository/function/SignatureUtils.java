package com.alibaba.druid.sql.repository.function;

import java.lang.reflect.Type;
import java.sql.Time;

public class SignatureUtils {
    public static Type getJavaType(String signature) {
        if (signature == null || signature.length() == 0) {
            return null;
        }

        char c0 = signature.charAt(0);
        if (signature.length() == 1) {
            switch (c0) {
                case 'b':
                    return byte.class;
                case 's':
                    return short.class;
                case 'i':
                    return int.class;
                case 'j':
                    return long.class;
                case 'f':
                    return float.class;
                case 'd':
                    return double.class;
                case 'c':
                    return char.class;

                case 'g':
                    return String.class; // not null
                case 'a':
                    return java.sql.Date.class; // not null
                case 't':
                    return Time.class; // not null
                case 'p':
                    return java.sql.Timestamp.class; // not null

                case 'B':
                    return Byte.class;
                case 'S':
                    return Short.class;
                case 'I':
                    return Integer.class;
                case 'J':
                    return Long.class;
                case 'F':
                    return Float.class;
                case 'D':
                    return Double.class;
                case 'C':
                    return Character.class;

                case 'G':
                    return String.class;
                case 'A':
                    return java.sql.Date.class;
                case 'T':
                    return Time.class;
                case 'P':
                    return java.sql.Timestamp.class;


                default:
                     break;
            }
        }

        throw new UnsupportedOperationException("type : " + signature + " is not support.");
    }
}
