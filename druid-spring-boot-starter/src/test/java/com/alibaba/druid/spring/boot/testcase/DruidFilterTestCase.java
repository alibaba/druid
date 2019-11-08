package com.alibaba.druid.spring.boot.testcase;

import java.util.List;

import javax.annotation.Resource;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.spring.boot.demo.DemoApplication;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author lihengming [89921218@qq.com]
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DemoApplication.class, DruidFilterTestCase.Config.class})
@ActiveProfiles("filter")
public class DruidFilterTestCase {
    @Resource
    private DruidDataSource dataSource;

    @Test
    public void test() {
        List<Filter> filters = dataSource.getProxyFilters();
        //配置文件中3个，自定义1个，共4个
        assertThat(filters.size()).isEqualTo(4);
    }

    /**
     * @author dk
     * 用于此测试的一个配置，仅加入了一个自定义的Filter，此Filter打印出数据库连接url
     */
    @Configuration
    @ComponentScan
    public static class Config{

        /**
         * @author dk
         */
        @Component
        public static class SomeCustomFilter extends FilterAdapter {

            private static Logger logger = LoggerFactory.getLogger(SomeCustomFilter.class);

            @Override
            public void init(DataSourceProxy dataSourceProxy){
                if (!(dataSourceProxy instanceof DruidDataSource)) {
                    logger.error("ConfigLoader only support DruidDataSource");
                }
                DruidDataSource dataSource = (DruidDataSource) dataSourceProxy;
                logger.info("db configuration: url="+ dataSource.getUrl());
            }
        }
    }
}
