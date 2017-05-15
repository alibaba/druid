package org.springframework.boot.autoconfigure.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServlet;
import javax.sql.DataSource;
import javax.sql.XADataSource;

/**
 * druid auto configuration
 *
 * @author leijuan
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
@ConditionalOnClass(DruidDataSource.class)
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(DataSourceProperties.class)
public class DruidAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    @ConditionalOnMissingBean({DataSource.class, XADataSource.class})
    public DataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().type(DruidDataSource.class).build();
    }

    @Bean
    @ConditionalOnClass(HttpServlet.class)
    public ServletRegistrationBean druidStatViewServlet() {
        return new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
    }

}
