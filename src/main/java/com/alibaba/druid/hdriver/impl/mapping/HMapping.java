package com.alibaba.druid.hdriver.impl.mapping;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Result;

public interface HMapping {

    byte[] getFamily(String columnName);

    byte[] getQualifier(String columnName);

    byte[] getRow(Result result, String columnName);

    boolean isRow(String columnName);

    Object getObject(Result result, String columnName);

    byte[] toBytes(String columnName, Object value) throws IOException;
}
