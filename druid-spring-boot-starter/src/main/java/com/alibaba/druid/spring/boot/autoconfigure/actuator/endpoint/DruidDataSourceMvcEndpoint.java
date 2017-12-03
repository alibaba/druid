/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.spring.boot.autoconfigure.actuator.endpoint;

import com.alibaba.druid.stat.DruidStatManagerFacade;

import org.springframework.boot.actuate.endpoint.mvc.AbstractMvcEndpoint;
import org.springframework.boot.actuate.endpoint.mvc.ActuatorMediaTypes;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author lihengming [89921218@qq.com]
 */
@ConfigurationProperties(prefix = "endpoints.druid")
public class DruidDataSourceMvcEndpoint extends AbstractMvcEndpoint {

    public DruidDataSourceMvcEndpoint() {
        super("/druid-endpoint", true);
    }

    @ResponseBody
    @GetMapping( produces = {ActuatorMediaTypes.APPLICATION_ACTUATOR_V1_JSON_VALUE,
        MediaType.APPLICATION_JSON_VALUE})
    public Object invoke() {
        return DruidStatManagerFacade.getInstance().getDataSourceStatDataList();
    }

}