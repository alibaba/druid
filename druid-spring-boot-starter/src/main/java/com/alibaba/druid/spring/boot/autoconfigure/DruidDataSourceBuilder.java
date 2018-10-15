/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.spring.boot.autoconfigure;


import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.core.env.Environment;

/**
 * @author lihengming [89921218@qq.com]
 */
public class DruidDataSourceBuilder {

    public static DruidDataSourceBuilder create() {
        return new DruidDataSourceBuilder();
    }

    /**
     * For build multiple DruidDataSource, detail see document.
     */
    public DruidDataSource build() {
        return new DruidDataSourceWrapper();
    }

    /**
     * For issue #1796, use Spring Environment by specify configuration properties prefix to build DruidDataSource.
     * <p>
     * 这是为了兼容 Spring Boot 1.X 中 .properties 内配置属性不能按照配置声明顺序进行绑定，进而导致配置出错（issue #1796 ）而提供的方法。
     * 如果你不存在上述问题或者使用 .yml 进行配置则不必使用该方法，使用上面的{@link DruidDataSourceBuilder#build}即可，Spring Boot 2.0 修复了该问题，该方法届时也会停用。
     */
    public DruidDataSource build(Environment env, String prefix) {
        DruidDataSource druidDataSource = new DruidDataSourceWrapper();
        druidDataSource.setMinEvictableIdleTimeMillis(
                env.getProperty(prefix + "min-evictable-idle-time-millis",
                        Long.class,
                        DruidDataSource.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS));
        druidDataSource.setMaxEvictableIdleTimeMillis(
                env.getProperty(prefix + "max-evictable-idle-time-millis",
                        Long.class,
                        DruidDataSource.DEFAULT_MAX_EVICTABLE_IDLE_TIME_MILLIS));
        return druidDataSource;
    }
}
