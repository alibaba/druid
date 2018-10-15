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

package com.alibaba.druid.support.simplejndi;

import java.sql.SQLException;
import java.util.Properties;

import org.osjava.sj.loader.convert.Converter;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

/**
 * 支持simple-jndi
 * <h1>配置示例：</h1>
 * <pre>
pgDruidTest/converter=com.alibaba.druid.support.simplejndi.DruidDataSourceConverter
pgDruidTest/type=javax.sql.DataSource
pgDruidTest/driverClassName=org.postgresql.Driver
pgDruidTest/url=jdbc:postgresql://127.0.0.1:5432/kettleRep
pgDruidTest/username=postgres
pgDruidTest/password=postgres
pgDruidTest/maxActive=50
pgDruidTest/minIdle=10
pgDruidTest/initialSize=5
pgDruidTest/validationQuery=SELECT 1
pgDruidTest/maxWait=10000
pgDruidTest/removeabandoned=true
pgDruidTest/removeabandonedtimeout=60
pgDruidTest/logabandoned=false
pgDruidTest/filters=stat,config,log4j
pgDruidTest/connectionProperties=druid.log.stmt.executableSql=true
   </pre>
 * date: 2016年1月31日 下午12:54:10 
 * @author jinjuma@yeah.net
 */
public class DruidDataSourceConverter implements Converter {

    private final static Log LOG = LogFactory.getLog(DruidDataSourceConverter.class);
	/**
	 * 
	 * @see org.osjava.sj.loader.convert.Converter#convert(java.util.Properties, java.lang.String)
	 */
	@Override
	public Object convert(Properties properties, String type) {
        try {
            DruidDataSource dataSource = new DruidDataSource();
        	DruidDataSourceFactory.config(dataSource, properties);
        	return dataSource;
		} catch (SQLException e) {
			LOG.error("properties:"+properties, e);
		}
        return null;
	}

}
