package com.alibaba.druid.admin.model;

import lombok.Data;

/**
 * @author linchtech
 * @date 2020-09-16 14:08
 **/
@Data
public class ServiceNode {

    private String id;

    private Integer port;

    private String address;

    private String serviceName;

}
