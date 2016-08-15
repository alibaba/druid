/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.visitor.functions;

import static com.alibaba.druid.sql.visitor.SQLEvalVisitor.EVAL_VALUE;
import static com.alibaba.druid.sql.visitor.SQLEvalVisitor.EVAL_VALUE_NULL;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.visitor.SQLEvalVisitor;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.Utils;

public class OneParamFunctions implements Function {

    public final static OneParamFunctions instance = new OneParamFunctions();

    public Object eval(SQLEvalVisitor visitor, SQLMethodInvokeExpr x) {
        if (x.getParameters().size() == 0) {
            return SQLEvalVisitor.EVAL_ERROR;
        }

        SQLExpr param = x.getParameters().get(0);
        param.accept(visitor);

        Object paramValue = param.getAttributes().get(EVAL_VALUE);
        if (paramValue == null) {
            return SQLEvalVisitor.EVAL_ERROR;
        }

        if (paramValue == EVAL_VALUE_NULL) {
            return EVAL_VALUE_NULL;
        }

        String method = x.getMethodName();
        if ("md5".equalsIgnoreCase(method)) {
            String text = paramValue.toString();
            return Utils.md5(text);
        }

        if ("bit_count".equalsIgnoreCase(method)) {
            if (paramValue instanceof BigInteger) {
                return ((BigInteger) paramValue).bitCount();
            }

            if (paramValue instanceof BigDecimal) {
                BigDecimal decimal = (BigDecimal) paramValue;
                BigInteger bigInt = decimal.setScale(0,  BigDecimal.ROUND_HALF_UP).toBigInteger();
                return bigInt.bitCount();
            }
            Long val = SQLEvalVisitorUtils.castToLong(paramValue);
            return Long.bitCount(val);
        }
        
        if ("soundex".equalsIgnoreCase(method)) {
            String text = paramValue.toString();
            return soundex(text);
        }
        
        if ("space".equalsIgnoreCase(method)) {
            int intVal = SQLEvalVisitorUtils.castToInteger(paramValue);
            char[] chars = new char[intVal];
            for (int i = 0; i < chars.length; ++i) {
                chars[i] = ' ';
            }
            return new String(chars);
        }

        throw new UnsupportedOperationException(method);
    }

    public static String soundex(String str) {
        if (str == null) {
            return null;
        }
        str = clean(str);
        if (str.length() == 0) {
            return str;
        }
        char out[] = {'0', '0', '0', '0'};
        char last, mapped;
        int incount = 1, count = 1;
        out[0] = str.charAt(0);
        // getMappingCode() throws IllegalArgumentException
        last = getMappingCode(str, 0);
        while ((incount < str.length()) && (count < out.length)) {
            mapped = getMappingCode(str, incount++);
            if (mapped != 0) {
                if ((mapped != '0') && (mapped != last)) {
                    out[count++] = mapped;
                }
                last = mapped;
            }
        }
        return new String(out);
    }
    
    static String clean(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        int len = str.length();
        char[] chars = new char[len];
        int count = 0;
        for (int i = 0; i < len; i++) {
            if (Character.isLetter(str.charAt(i))) {
                chars[count++] = str.charAt(i);
            }
        }
        if (count == len) {
            return str.toUpperCase(java.util.Locale.ENGLISH);
        }
        return new String(chars, 0, count).toUpperCase(java.util.Locale.ENGLISH);
    }
    
    private static char getMappingCode(String str, int index) {
        // map() throws IllegalArgumentException
        char mappedChar = map(str.charAt(index));
        // HW rule check
        if (index > 1 && mappedChar != '0') {
            char hwChar = str.charAt(index - 1);
            if ('H' == hwChar || 'W' == hwChar) {
                char preHWChar = str.charAt(index - 2);
                char firstCode = map(preHWChar);
                if (firstCode == mappedChar || 'H' == preHWChar || 'W' == preHWChar) {
                    return 0;
                }
            }
        }
        return mappedChar;
    }
    
    private static char map(char ch) {
        String soundexMapping = "01230120022455012623010202";
        int index = ch - 'A';
        if (index < 0 || index >= soundexMapping.length()) {
            throw new IllegalArgumentException("The character is not mapped: " + ch);
        }
        return soundexMapping.charAt(index);
    }
    
    
}
