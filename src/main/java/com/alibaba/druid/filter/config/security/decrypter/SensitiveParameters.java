package com.alibaba.druid.filter.config.security.decrypter;

/**
 * <pre>
 * 存放， 数据库的关键参数：URL， 用户名， 密码
 * 如果存放的是明文， 请尽可能缩短实例的作用域
 * </pre>
 *
 * @author Jonas Yang
 */
public class SensitiveParameters {
    private transient final String url;

    private transient final String username;

    private transient final String password;

    public SensitiveParameters(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
