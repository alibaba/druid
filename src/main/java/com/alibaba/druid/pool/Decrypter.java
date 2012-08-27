package com.alibaba.druid.pool;

/**
 * <pre>
 * 解密敏感参数
 * 使用解密器 只要简单实现这个类就可以了。
 *
 * public class MyDecrypter implement Decrypter {
 *     public SensitiveParameters decrypt(SensitiveParameters parameters) throws DecryptException {
 *         String url = ....;
 *         String username = ....;
 *         String password = ....;
 *
 *         return new SensitiveParameters(url, username, password);
 *     }
 * }
 *
 * DruidDataSource dataSource = new DruidDataSource();
 * //设置DataSource的其他参数
 * dataSource.setDecrypter(new MyDecrypter);
 *
 * [下面是Spring的配置]
 *
 * &lt;bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource" init-method="init" destroy-method="close"&gt;
 *     &lt;property name="url" value="${jdbc_url}" /&gt;
 *     &lt;property name="username" value="${jdbc_user}" /&gt;
 *     &lt;property name="password" value="${jdbc_password}" /&gt;
 *     <b>&lt;property name="decrypter"&gt;&lt;ref bean="myDecrypter"/&gt;&lt;/property&gt;</b>
 * &lt;/bean&gt;
 *
 * &lt;bean id="myDecrypter" class="MyDecrypter"/&gt;
 *
 * </pre>
 * <br/>
 * <b>默认下, Druid 已经实现了AES, DES, RSA, Blowfish(兼容JBOSS)等加解密. AES, DES 解密的密文必须是通过Druid Tool的加密工具生成的</b>
 * <br/>
 * <b>使用加密工具: java -cp druid-x.x.x.jar com.alibaba.druid.support.security.tool.Main</b>
 * @author Jonas Yang
 */
public interface Decrypter {

    /**
     * 返回一个解密后的（明文）参数
     * @param parameters 密文参数
     * @return
     * @throws DecryptException 解密失败会抛出该异常
     */
    public SensitiveParameters decrypt(SensitiveParameters parameters) throws DecryptException;
}
