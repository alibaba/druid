/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.filter.config.security.decrypter;

/**
 * 加密出错
 *
 * @author Jonas Yang
 */
public class DecryptException extends Exception {
    private static final long serialVersionUID = 1L;

    public DecryptException() {
        super();
    }

    public DecryptException(String s) {
        super(s);
    }

    public DecryptException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public DecryptException(Throwable throwable) {
        super(throwable);
    }
}
