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
package com.alibaba.druid.pool;

import java.sql.SQLException;
import java.util.Properties;

/**
 * An interface to allow for exception evaluation.
 */
public interface ExceptionSorter {

    /**
     * Returns true or false whether or not the exception is fatal.
     * 
     * @param e the exception
     * @return true or false if the exception is fatal.
     */
    boolean isExceptionFatal(SQLException e);
    
    void configFromProperties(Properties properties);
}
