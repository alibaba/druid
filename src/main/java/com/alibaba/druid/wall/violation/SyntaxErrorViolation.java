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

import com.alibaba.druid.wall.Violation;

public class SyntaxErrorViolation implements Violation {

    private final Exception exception;

    private final String    sql;

    public SyntaxErrorViolation(Exception exception, String sql){
        super();
        this.exception = exception;
        this.sql = sql;
    }

    public String toString() {
        return this.sql;
    }

    public Exception getException() {
        return exception;
    }

    public String getSql() {
        return sql;
    }

    public String getMessage() {
        if (exception == null) {
            return "syntax error";
        }

        return "syntax error: " + exception.getMessage();
    }

    @Override
    public int getErrorCode() {
        return ErrorCode.SYNTAX_ERROR;
    }
}
