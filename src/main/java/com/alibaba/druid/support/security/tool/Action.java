package com.alibaba.druid.support.security.tool;

/**
 * @author Jonas Yang
 */
public interface Action {
    /**
     * DES, AES 密钥都有长度限制， 如果不够， 默认使用该字段补长
     */
    public String KEY_PADDING = "FOLLOW YOUR HEART. YOU CAN DO BEST THAN ANY ONE.";

    public String getId();

    public void execute();
}
