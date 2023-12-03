package com.alibaba.druid.pool;

import junit.framework.TestCase;

public class DruidDataSourceTest extends TestCase {

    /**
     * 验证将mysql jdbc url中可能出现的密码信息全都掩码的效果，目前会出现的密码key名有password,password1,password2,password3,trustCertificateKeyStorePassword,clientCertificateKeyStorePassword
     * @see  <a href="https://dev.mysql.com/doc/connector-j/en/connector-j-connp-props-authentication.html">...</a>
     * @see <a href="https://dev.mysql.com/doc/connector-j/en/connector-j-connp-props-security.html">...</a>
     */
    public void test_sanitizedUrl() {
        String url = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        String expectedUrl = url;
        String urlNew = DruidDataSource.sanitizedUrl(url);
        System.out.println("原始url=" + url);
        System.out.println("掩码url=" + urlNew);
        assertEquals(expectedUrl, urlNew);

        url = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password";
        expectedUrl = url;
        urlNew = DruidDataSource.sanitizedUrl(url);
        System.out.println("原始url=" + url);
        System.out.println("掩码后url=" + urlNew);
        assertEquals(expectedUrl, urlNew);

        url = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password=";
        expectedUrl = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password=<masked>";
        urlNew = DruidDataSource.sanitizedUrl(url);
        System.out.println("原始url=" + url);
        System.out.println("掩码后url=" + urlNew);
        assertEquals(expectedUrl, urlNew);

        url = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password=12345678";
        expectedUrl = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password=<masked>";
        urlNew = DruidDataSource.sanitizedUrl(url);
        System.out.println("原始url=" + url);
        System.out.println("掩码后url=" + urlNew);
        assertEquals(expectedUrl, urlNew);

        url = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password=12345678&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";

        expectedUrl = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password=<masked>&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        urlNew = DruidDataSource.sanitizedUrl(url);
        System.out.println("原始url=" + url);
        System.out.println("掩码后url=" + urlNew);
        assertEquals(expectedUrl, urlNew);

        url = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password1=12345678&password=12345678&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        expectedUrl = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password1=<masked>&password=<masked>&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";

        urlNew = DruidDataSource.sanitizedUrl(url);
        System.out.println("原始url=" + url);
        System.out.println("掩码后url=" + urlNew);
        assertEquals(expectedUrl, urlNew);

        url = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password2=12345678&password1=12345678&password=12345678&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        expectedUrl = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password2=<masked>&password1=<masked>&password=<masked>&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        urlNew = DruidDataSource.sanitizedUrl(url);
        System.out.println("原始url=" + url);
        System.out.println("掩码后url=" + urlNew);
        assertEquals(expectedUrl, urlNew);

        url = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password3=12345678&password2=12345678&password1=12345678&password=12345678&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        expectedUrl = "jdbc:mysql://127.0.0.1:3306/druid?useUnicode=true&user=root&password3=<masked>&password2=<masked>&password1=<masked>&password=<masked>&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        urlNew = DruidDataSource.sanitizedUrl(url);
        System.out.println("原始url=" + url);
        System.out.println("掩码后url=" + urlNew);
        assertEquals(expectedUrl, urlNew);

        url = "jdbc:mysql://127.0.0.1:3306/druid?clientCertificateKeyStorePassword=12345678&useUnicode=true&user=root&password3=12345678&password2=12345678&password1=12345678&password=12345678&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        expectedUrl = "jdbc:mysql://127.0.0.1:3306/druid?clientCertificateKeyStorePassword=<masked>&useUnicode=true&user=root&password3=<masked>&password2=<masked>&password1=<masked>&password=<masked>&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        urlNew = DruidDataSource.sanitizedUrl(url);
        System.out.println("原始url=" + url);
        System.out.println("掩码后url=" + urlNew);
        assertEquals(expectedUrl, urlNew);

        url = "jdbc:mysql://127.0.0.1:3306/druid?trustCertificateKeyStorePassword=12345678&useUnicode=true&user=root&password3=12345678&password2=12345678&password1=12345678&password=12345678&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        expectedUrl = "jdbc:mysql://127.0.0.1:3306/druid?trustCertificateKeyStorePassword=<masked>&useUnicode=true&user=root&password3=<masked>&password2=<masked>&password1=<masked>&password=<masked>&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true";
        urlNew = DruidDataSource.sanitizedUrl(url);
        System.out.println("原始url=" + url);
        System.out.println("掩码后url=" + urlNew);
        assertEquals(expectedUrl, urlNew);


    }
}