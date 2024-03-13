package com.alibaba.druid.spring.boot.testcase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.demo.DemoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author lihengming [89921218@qq.com]
 *
 * for issue #1796, #3084, #2763
 */
@SpringBootTest(classes = DemoApplication.class)
@ActiveProfiles("issue-1796")
public class Issue1796TestCase {
    @Resource
    private DruidDataSource dataSource;

    @Test
    public void test() {
        assertThat(dataSource.getUrl()).isEqualTo("jdbc:h2:file:./demo-db;NON_KEYWORDS=user");
        assertThat(dataSource.getUsername()).isEqualTo("sa");
        assertThat(dataSource.getPassword()).isEqualTo("sa");
        assertThat(dataSource.getDriverClassName()).isEqualTo("org.h2.Driver");

        assertThat(dataSource.getMinEvictableIdleTimeMillis()).isEqualTo(100000);
        assertThat(dataSource.getMaxEvictableIdleTimeMillis()).isEqualTo(200000);
    }
}
