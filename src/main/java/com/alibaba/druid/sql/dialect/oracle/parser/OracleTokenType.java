/*
 * Copyright 2011 Alibaba Group.
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
package com.alibaba.druid.sql.dialect.oracle.parser;

public final class OracleTokenType {

    public static final int LineComment      = 0;
    public static final int Identifier       = 1;
    public static final int Variable         = 2;
    public static final int Keyword          = 3;
    public static final int Operator         = 4;
    public static final int Punctuation      = 5;
    public static final int Char             = 6;
    public static final int NChar            = 7;
    public static final int Int              = 8;
    public static final int Float            = 9;
    public static final int Double           = 10;
    public static final int Decimal          = 11;
    public static final int EOF              = 12;
    public static final int Unknown          = 13;
    public static final int MultiLineComment = 14;
    public static final int Long             = 15;
    public static final int HINT             = 16;

    public static final String typename(int tokType) {
        switch (tokType) {
            case 0:
                return "Comment";
            case 1:
                return "Identifier";
            case 2:
                return "Variable";
            case 3:
                return "Keyword";
            case 4:
                return "Operator";
            case 5:
                return "Punctuation";
            case 6:
                return "Char";
            case 7:
                return "NChar";
            case 8:
                return "Int";
            case 9:
                return "Float";
            case 10:
                return "Double";
            case 11:
                return "Decimal";
            case 12:
                return "EOF";
            case 16:
                return "Hints";
            case 13:
            case 14:
            case 15:
        }
        return "Unknown";
    }
}
