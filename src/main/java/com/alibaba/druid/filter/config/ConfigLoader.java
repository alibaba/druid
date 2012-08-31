package com.alibaba.druid.filter.config;

import java.util.Properties;

/**
 * <pre>
 * 配置文件装载器.
 *
 * 要实现自己的装载器有如下要求.
 * 1. 一定要有自己的协议格式, 例如是由 my-protocol: 开头的protocol是由我支持的.
 * public isSupported(String protocol) {
 *     return protocol.startsWith("my-protocol:");
 * }
 *
 * 2. 要在class path的根目录下的ConfigLoader.properties注册(如果没有这个文件, 可以自己创建一个). 例如:
 * WEB-INF/classes/ConfigLoader.properties [格式如下]:
 * ConfigLoader.myConfigLoader=com.xxx.xxx.xx.MyConfigLoader
 *
 * 3. 配置<code>DruidDataSource</code>的时候的URL就要设置为装载器的协议格式
 * DruidDataSource dataSource = new DruidDataSource();
 * dataSource.setUrl("my-protocol:http://xxxxxx/");
 *
 * </pre>
 * @see com.alibaba.druid.filter.config.impl.FileConfigLoader
 * @see com.alibaba.druid.filter.config.impl.HttpConfigLoader
 *
 * @author Jonas Yang
 */
public interface ConfigLoader {

    /**
     * 唯一的ID
     * @return
     */
    public String getId();

    /**
     * 获得配置内容, 如果没有, 返回<code>null</code>.
     * @param protocol
     * @return
     */
    public Properties loadConfig(String protocol);

    /**
     * 是否支持该协议, 如果支持, 将会使用该装载器获取配置文件
     * @param protocol
     * @return
     */
    public boolean isSupported(String protocol);
}
