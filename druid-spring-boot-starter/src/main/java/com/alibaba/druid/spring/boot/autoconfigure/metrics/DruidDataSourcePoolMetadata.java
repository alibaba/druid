package com.alibaba.druid.spring.boot.autoconfigure.metrics;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.jdbc.metadata.AbstractDataSourcePoolMetadata;

/**
 * @author hai
 * @version 1.0
 * Created on 2021/11/16 下午7:15
 * @description
 */
public class DruidDataSourcePoolMetadata extends AbstractDataSourcePoolMetadata<DruidDataSource> {

  /**
   * Create an instance with the data source to use.
   *
   * @param dataSource the data source
   */
  public DruidDataSourcePoolMetadata(DruidDataSource dataSource) {
    super(dataSource);
  }

  @Override
  public Integer getActive() {
    return getDataSource().getActiveCount();
  }

  @Override
  public Integer getMax() {
    return getDataSource().getMaxActive();
  }

  @Override
  public Integer getMin() {
    return getDataSource().getMinIdle();
  }

  @Override
  public String getValidationQuery() {
    return getDataSource().getValidationQuery();
  }

  @Override
  public Boolean getDefaultAutoCommit() {
    return getDataSource().isDefaultAutoCommit();
  }
}
