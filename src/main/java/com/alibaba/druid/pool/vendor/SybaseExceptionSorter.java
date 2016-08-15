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
package com.alibaba.druid.pool.vendor;

import com.alibaba.druid.pool.ExceptionSorter;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Properties;

public class SybaseExceptionSorter implements ExceptionSorter, Serializable {

    private static final long serialVersionUID = 2742592563671255116L;
    
    public SybaseExceptionSorter() {
        this.configFromProperties(System.getProperties());
    }

    public boolean isExceptionFatal(SQLException e) {
        boolean result = false;

        String errorText = e.getMessage();
        if (errorText == null) {
            return false;
        }
        errorText = errorText.toUpperCase();

        if ((errorText.contains("JZ0C0")) || // ERR_CONNECTION_DEAD
            (errorText.contains("JZ0C1")) // ERR_IOE_KILLED_CONNECTION
        ) {
            result = true;
        }

        return result;
    }
    
    public void configFromProperties(Properties properties) {
        
    }
}
