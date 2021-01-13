/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.FastsqlException;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.Utils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

public class SQLBinaryExpr extends SQLExprImpl implements SQLLiteralExpr, SQLValuableExpr {

    private String text;

    private transient Number val;

    public SQLBinaryExpr(){

    }

    public SQLBinaryExpr(String value){
        super();
        this.text = value;
    }

    public String getText() {
        return text;
    }

    public Number getValue() {
        if (text == null) {
            return null;
        }

        if (val == null) {
            long[] words = new long[text.length() / 64 + 1];
            for (int i = text.length() - 1; i >= 0; --i) {
                char ch = text.charAt(i);
                if (ch == '1') {
                    int wordIndex = i >> 6;
                    words[wordIndex] |= (1L << (text.length() - 1 - i));
                }
            }

            if (words.length == 1) {
                val = words[0];
            } else {
                byte[] bytes = new byte[words.length * 8];

                for (int i = 0; i < words.length; ++i) {
                    Utils.putLong(bytes, (words.length - 1 - i) * 8, words[i]);
                }

                val = new BigInteger(bytes);
            }
        }

        return val;
    }

    public void setValue(String value) {
        this.text = value;
    }

    public void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }

    public void output(Appendable buf) {
        try {
            buf.append("b'");
            buf.append(text);
            buf.append('\'');
        } catch (IOException ex) {
            throw new FastsqlException("output error", ex);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        return result;
    }

    public SQLBinaryExpr clone() {
        return new SQLBinaryExpr(text);
    }

    @Override
    public List<SQLObject> getChildren() {
        return Collections.emptyList();
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
        SQLBinaryExpr other = (SQLBinaryExpr) obj;
        if (text == null) {
            if (other.text != null) {
                return false;
            }
        } else if (!text.equals(other.text)) {
            return false;
        }
        return true;
    }

}
