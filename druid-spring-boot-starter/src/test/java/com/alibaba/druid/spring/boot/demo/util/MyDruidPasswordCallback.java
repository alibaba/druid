package com.alibaba.druid.spring.boot.demo.util;

import com.alibaba.druid.util.DruidPasswordCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Properties;

/**
 * 数据库密码回调解密
 *
 * @author Created by 思伟 on 2020/7/22
 */
public class MyDruidPasswordCallback extends DruidPasswordCallback {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**
     * 做个缓存，防止一直请求
     */
    private String password = null;

    @Override
    public void setProperties(Properties properties) {
        if (!StringUtils.isEmpty(password)) {
            // 程序应只在启动时调用密码解密，之后保存在内存中，不能每次使用都调用接口获取密码
            setPassword(password.toCharArray());
            return;
        }
        Assert.notNull(properties, "Properties must not be null");
        super.setProperties(properties);
        password = properties.getProperty("password");
        if (!StringUtils.isEmpty(password)) {
            try {
                // 这里的password是将配置得到的密码进行解密之后的值
                setPassword(StandAloneUtil.decrypt(password).toCharArray());
            } catch (Exception ex) {
                // 报错了不做异常抛出，有可能是本地测试密码不需要解密
                LOGGER.warn("数据库密文解密失败，跳过......");
                setPassword(password.toCharArray());
            }
        }
    }

    /**
     * 数据库密文帮助类
     *
     * @author Created by 思伟 on 2020/7/22
     */
    private static class StandAloneUtil {
        /**
         * 解密
         */
        public static String decrypt(String encryption) throws Exception {
            return encryption;
        }

        /**
         * 加密
         */
        public static String encrypt(String encryption) throws Exception {
            return encryption;
        }

    }

}
