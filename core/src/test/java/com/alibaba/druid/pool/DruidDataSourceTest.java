package com.alibaba.druid.pool;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class DruidDataSourceTest {
    /**
     * 验证将mysql jdbc url中可能出现的密码信息全都掩码的效果，目前会出现的密码key名有password,password1,password2,password3,trustCertificateKeyStorePassword,clientCertificateKeyStorePassword
     * @see  <a href="https://dev.mysql.com/doc/connector-j/en/connector-j-connp-props-authentication.html">...</a>
     * @see <a href="https://dev.mysql.com/doc/connector-j/en/connector-j-connp-props-security.html">...</a>
     */
    @Test
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

    @Test
    public void test_clone_copiesCoreConfiguration() throws Exception {
        DruidDataSource dataSource = createCloneSource();

        DruidDataSource clone = (DruidDataSource) dataSource.clone();

        assertNotSame(dataSource, clone);
        assertEquals(dataSource.getUrl(), clone.getUrl());
        assertEquals(dataSource.getUsername(), clone.getUsername());
        assertEquals(dataSource.getPassword(), clone.getPassword());
        assertEquals(dataSource.getMaxActive(), clone.getMaxActive());
        assertEquals(dataSource.getMinIdle(), clone.getMinIdle());
        assertEquals(dataSource.getMaxIdle(), clone.getMaxIdle());
        assertEquals(dataSource.getMaxWait(), clone.getMaxWait());
        assertEquals(dataSource.isDefaultAutoCommit(), clone.isDefaultAutoCommit());
        assertEquals(dataSource.getConnectionInitSqls(), clone.getConnectionInitSqls());
        assertEquals(dataSource.getFilterClassNames(), clone.getFilterClassNames());
    }

    @Test
    public void test_cloneDruidDataSource_hasIndependentMutableCollections() throws Exception {
        DruidDataSource dataSource = createCloneSource();

        DruidDataSource clone = dataSource.cloneDruidDataSource();

        assertNotSame(dataSource, clone);
        assertNotSame(dataSource.getProxyFilters(), clone.getProxyFilters());
        assertNotSame(dataSource.getConnectionInitSqls(), clone.getConnectionInitSqls());

        Collection<String> cloneInitSqls = clone.getConnectionInitSqls();
        cloneInitSqls.add("set @cloned=1");

        assertEquals(2, dataSource.getConnectionInitSqls().size());
        assertEquals(3, clone.getConnectionInitSqls().size());
    }

    private static DruidDataSource createCloneSource() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.alibaba.druid.mock.MockDriver");
        dataSource.setUrl("jdbc:mock:clone-test");
        dataSource.setUsername("clone-user");
        dataSource.setPassword("clone-password");
        dataSource.setMaxActive(7);
        dataSource.setMinIdle(2);
        dataSource.setMaxIdle(6);
        dataSource.setMaxWait(3456);
        dataSource.setDefaultAutoCommit(false);
        dataSource.setConnectionInitSqls(Arrays.asList("set names utf8mb4", "set sql_mode='STRICT_TRANS_TABLES'"));
        dataSource.setFilters("stat");

        Properties connectProperties = new Properties();
        connectProperties.setProperty("socketTimeout", "5000");
        connectProperties.setProperty("connectTimeout", "3000");
        dataSource.setConnectProperties(connectProperties);

        List<String> filterClassNames = dataSource.getFilterClassNames();
        assertEquals(1, filterClassNames.size());

        return dataSource;
    }
}
