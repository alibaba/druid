package com.alibaba.druid.support.security.tool;

import com.alibaba.druid.support.security.decryptor.AbstractDecrypter;

/**
 * @author Jonas Yang
 */
public interface Action {
    /**
     * DES, AES 密钥都有长度限制， 如果不够， 默认使用该字段补长
     */
    public String KEY_PADDING = AbstractDecrypter.KEY_PADDING;

    public String getId();

    public void execute();
}
