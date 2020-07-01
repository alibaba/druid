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
package com.alibaba.druid.proxy.jdbc;

import java.util.Calendar;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public interface JdbcParameter {

    public static final int BinaryInputStream     = 10001;
    public static final int AsciiInputStream      = 10002;
    public static final int CharacterInputStream  = 10003;
    public static final int NCharacterInputStream = 10004;
    public static final int URL                   = 10005;

    public static interface TYPE {

        public static final int BinaryInputStream     = 10001;
        public static final int AsciiInputStream      = 10002;
        public static final int CharacterInputStream  = 10003;
        public static final int NCharacterInputStream = 10004;
        public static final int URL                   = 10005;
        public static final int UnicodeStream         = 10006;
        public static final int BYTES                 = 10007;

    }

    Object getValue();

    long getLength();

    Calendar getCalendar();

    int getSqlType();
}
