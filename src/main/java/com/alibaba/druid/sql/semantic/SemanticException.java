package com.alibaba.druid.sql.semantic;

import com.alibaba.druid.FastsqlException;

public class SemanticException extends FastsqlException  {
    public SemanticException(String message) {
        super(message);
    }
}
