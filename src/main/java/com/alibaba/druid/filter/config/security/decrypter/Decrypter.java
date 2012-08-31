package com.alibaba.druid.filter.config.security.decrypter;

import java.util.Properties;

/**
 * <pre>
 * 解密敏感参数
 * [代码例子]
 *
 * DruidDataSource dataSource = new DruidDataSource();
 * //设置DataSource的其他参数
 * dataSource.setFilters("config");
 * dataSource.setUsername("test");
 * dataSource.setPassword("加密的密码");
 * dataSource.setConnectionProperties("config.decrypt=RSA");
 *
 * [下面是Spring的配置]
 *
 * &lt;bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource" init-method="init" destroy-method="close"&gt;
 *     &lt;property name="url" value="${jdbc_url}" /&gt;
 *     &lt;property name="username" value="${jdbc_user}" /&gt;
 *     &lt;property name="password" value="${jdbc_password}" /&gt;
 *     <b>&lt;property name="filters" value="config" /&gt;</b>
 *     <b>&lt;property name="connectionProperties" value="config.decrypt=RSA" /&gt;</b>
 * &lt;/bean&gt;
 *
 * </pre>
 * <br/>
 * <b>默认下, Druid 已经实现了RSA加解密. RSA 密文必须是通过Druid Tool的加密工具生成的</b>
 * <b>获取默认解密方式可以通过 DecrypterFactory.getDecrypter(String id)</b>
 * <br/>
 * <b>使用加密工具: java -cp druid-x.x.x.jar com.alibaba.druid.filter.config.security.tool.Main</b>
 * @author Jonas Yang
 */
public interface Decrypter {

    /**
     * 标识符
     * @return
     */
    public String getId();

    /**
     * 返回一个解密后的（明文）参数
     * @param parameters 密文参数
     * @return
     * @throws DecryptException 解密失败会抛出该异常
     */
    public SensitiveParameters decrypt(SensitiveParameters parameters, Properties info) throws DecryptException;
}
