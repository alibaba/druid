package com.alibaba.druid.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author 107
 * @date 2020-09-16 17:10
 **/
@Data
@ConfigurationProperties(prefix = "monitor")
public class MonitorProperties {

    /**
     * 需要监控的服务
     */
    private List<String> applications;
    /**
     * 登录用户名
     */
    private String loginUsername;
    /**
     * 登录密码
     */
    private String loginPassword;

}
