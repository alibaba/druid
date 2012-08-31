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

import com.alibaba.druid.util.Base64;

import javax.crypto.Cipher;

/**
 * 加密抽象类
 *
 * @author Jonas Yang
 */
public abstract class AbstractDecrypter implements Decrypter {

    public final static String KEY = "config.decrypt.key";

    /**
     * 传入密文， 返回明文
     * @param cipher 解密器
     * @param cipherString 密文
     * @return
     * @throws Exception
     */
    protected String decrypt(Cipher cipher, String cipherString) throws Exception {
        if (cipherString == null || cipherString.length() == 0) {
            return cipherString;
        }

        byte[] cipherBytes = Base64.base64ToByteArray(cipherString);
        byte[] plainBytes = cipher.doFinal(cipherBytes);

        return new String(plainBytes);
    }
}
