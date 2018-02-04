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

import com.alibaba.druid.wall.Violation;

public class IllegalSQLObjectViolation implements Violation {

    private final String message;
    private final String sqlPart;
    private final int errorCode;

    public IllegalSQLObjectViolation(int errorCode, String message, String sqlPart){
        this.errorCode = errorCode;
        this.message = message;
        this.sqlPart = sqlPart;
    }

    public String getSqlPart() {
        return sqlPart;
    }

    public String toString() {
        return this.sqlPart;
    }

    
    public String getMessage() {
        return message;
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    
}
