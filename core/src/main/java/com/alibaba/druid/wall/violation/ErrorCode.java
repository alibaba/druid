/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.wall.violation;

public interface ErrorCode {
    public static final int SYNTAX_ERROR = 1001;
    public static final int SELECT_NOT_ALLOW = 1002;
    public static final int SELECT_INTO_NOT_ALLOW = 1003;
    public static final int INSERT_NOT_ALLOW = 1004;
    public static final int DELETE_NOT_ALLOW = 1005;
    public static final int UPDATE_NOT_ALLOW = 1006;
    public static final int MINUS_NOT_ALLOW = 1007;
    public static final int INTERSET_NOT_ALLOW = 1008;
    public static final int MERGE_NOT_ALLOW = 1009;
    public static final int REPLACE_NOT_ALLOW = 1010;

    public static final int HINT_NOT_ALLOW = 1400;

    public static final int CALL_NOT_ALLOW = 1300;
    public static final int COMMIT_NOT_ALLOW = 1301;
    public static final int ROLLBACK_NOT_ALLOW = 1302;
    public static final int START_TRANSACTION_NOT_ALLOW = 1303;
    public static final int BLOCK_NOT_ALLOW = 1304;

    public static final int SET_NOT_ALLOW = 1200;
    public static final int DESC_NOT_ALLOW = 1201;
    public static final int SHOW_NOT_ALLOW = 1202;
    public static final int USE_NOT_ALLOW = 1203;

    public static final int NONE_BASE_STATEMENT_NOT_ALLOW = 1999;

    public static final int TRUNCATE_NOT_ALLOW = 1100;
    public static final int CREATE_TABLE_NOT_ALLOW = 1101;
    public static final int ALTER_TABLE_NOT_ALLOW = 1102;
    public static final int DROP_TABLE_NOT_ALLOW = 1103;
    public static final int COMMENT_STATEMENT_NOT_ALLOW = 1104;
    public static final int RENAME_TABLE_NOT_ALLOW = 1105;
    public static final int LOCK_TABLE_NOT_ALLOW = 1106;

    public static final int LIMIT_ZERO = 2200;
    public static final int MULTI_STATEMENT = 2201;

    public static final int FUNCTION_DENY = 2001;
    public static final int SCHEMA_DENY = 2002;
    public static final int VARIANT_DENY = 2003;
    public static final int TABLE_DENY = 2004;
    public static final int OBJECT_DENY = 2005;

    public static final int ALWAYS_TRUE = 2100;
    public static final int CONST_ARITHMETIC = 2101;
    public static final int XOR = 2102;
    public static final int BITWISE = 2103;
    public static final int NONE_CONDITION = 2104;
    public static final int LIKE_NUMBER = 2105;
    public static final int EMPTY_QUERY_HAS_CONDITION = 2106;
    public static final int DOUBLE_CONST_CONDITION = 2107;
    public static final int SAME_CONST_LIKE = 2108;
    public static final int CONST_CASE_CONDITION = 2109;
    public static final int EVIL_HINTS = 2110;
    public static final int EVIL_NAME = 2111;
    public static final int EVIL_CONCAT = 2112;
    public static final int ALWAYS_FALSE = 2113;

    public static final int NOT_PARAMETERIZED = 2200;
    public static final int MULTI_TENANT = 2201;

    public static final int INTO_OUTFILE = 3000;

    public static final int READ_ONLY = 4000;
    public static final int UNION = 5000;
    public static final int INVALID_JOIN_CONDITION = 6000;

    public static final int COMPOUND = 8000;

    public static final int UPDATE_CHECK_FAIL = 9000;

    public static final int OTHER = 9999;
}
