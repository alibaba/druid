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
package com.alibaba.druid.wall.violation;

public interface ErrorCode {

    public final static int SYNTAX_ERROR                  = 1001;
    public final static int SELECT_NOT_ALLOW              = 1002;
    public final static int SELECT_INTO_NOT_ALLOW         = 1003;
    public final static int INSERT_NOT_ALLOW              = 1004;
    public final static int DELETE_NOT_ALLOW              = 1005;
    public final static int UPDATE_NOT_ALLOW              = 1006;
    public final static int MINUS_NOT_ALLOW               = 1007;
    public final static int INTERSET_NOT_ALLOW            = 1008;
    public final static int MERGE_NOT_ALLOW               = 1009;
    public final static int REPLACE_NOT_ALLOW             = 1010;
    
    public final static int HINT_NOT_ALLOW                = 1400;

    public final static int CALL_NOT_ALLOW                = 1300;
    public final static int COMMIT_NOT_ALLOW              = 1301;
    public final static int ROLLBACK_NOT_ALLOW            = 1302;
    public final static int START_TRANSACTION_NOT_ALLOW   = 1303;

    public final static int SET_NOT_ALLOW                 = 1200;
    public final static int DESC_NOT_ALLOW                = 1201;
    public final static int SHOW_NOT_ALLOW                = 1202;
    public final static int USE_NOT_ALLOW                 = 1203;

    public final static int NONE_BASE_STATEMENT_NOT_ALLOW = 1999;

    public final static int TRUNCATE_NOT_ALLOW            = 1100;
    public final static int CREATE_TABLE_NOT_ALLOW        = 1101;
    public final static int ALTER_TABLE_NOT_ALLOW         = 1102;
    public final static int DROP_TABLE_NOT_ALLOW          = 1103;
    public final static int COMMENT_STATEMENT_NOT_ALLOW   = 1104;
    public final static int RENAME_TABLE_NOT_ALLOW        = 1105;
    public final static int LOCK_TABLE_NOT_ALLOW          = 1106;

    public final static int LIMIT_ZERO                    = 2200;
    public final static int MULTI_STATEMENT               = 2201;

    public final static int FUNCTION_DENY                 = 2001;
    public final static int SCHEMA_DENY                   = 2002;
    public final static int VARIANT_DENY                  = 2003;
    public final static int TABLE_DENY                    = 2004;
    public final static int OBJECT_DENY                   = 2005;

    public final static int ALWAYS_TRUE                   = 2100;
    public final static int CONST_ARITHMETIC              = 2101;
    public final static int XOR                           = 2102;
    public final static int BITWISE                       = 2103;
    public final static int NONE_CONDITION                = 2104;
    public final static int LIKE_NUMBER                   = 2105;
    public final static int EMPTY_QUERY_HAS_CONDITION     = 2106;
    public final static int DOUBLE_CONST_CONDITION        = 2107;
    public final static int SAME_CONST_LIKE               = 2108;
    public final static int CONST_CASE_CONDITION          = 2109;
    public final static int EVIL_HINTS                    = 2110;
    public final static int EVIL_NAME                     = 2111;
    public final static int EVIL_CONCAT                   = 2112;
    public final static int ALWAYS_FALSE                  = 2113;

    public final static int NOT_PARAMETERIZED             = 2200;
    public final static int MULTI_TENANT                  = 2201;

    public final static int INTO_OUTFILE                  = 3000;

    public final static int READ_ONLY                     = 4000;
    public final static int UNION                         = 5000;

    public final static int COMPOUND                      = 8000;

    public final static int OTHER                         = 9999;
}
