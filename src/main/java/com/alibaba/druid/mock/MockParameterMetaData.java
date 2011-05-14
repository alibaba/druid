package com.alibaba.druid.mock;

import java.sql.ParameterMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MockParameterMetaData implements ParameterMetaData {

    public MockParameterMetaData(){

    }

    private final List<Parameter> parameters = new ArrayList<Parameter>();

    public List<Parameter> getParameters() {
        return parameters;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public int getParameterCount() throws SQLException {
        return parameters.size();
    }

    @Override
    public int isNullable(int param) throws SQLException {
        return parameters.get(param - 1).getNullable();
    }

    @Override
    public boolean isSigned(int param) throws SQLException {
        return parameters.get(param - 1).isSigned();
    }

    @Override
    public int getPrecision(int param) throws SQLException {
        return parameters.get(param - 1).getPrecision();
    }

    @Override
    public int getScale(int param) throws SQLException {
        return parameters.get(param - 1).getScale();
    }

    @Override
    public int getParameterType(int param) throws SQLException {
        return parameters.get(param - 1).getType();
    }

    @Override
    public String getParameterTypeName(int param) throws SQLException {
        return parameters.get(param - 1).getTypeName();
    }

    @Override
    public String getParameterClassName(int param) throws SQLException {
        return parameters.get(param - 1).getClassName();
    }

    @Override
    public int getParameterMode(int param) throws SQLException {
        return parameters.get(param - 1).getMode();
    }

    public static class Parameter {

        private int     nullable;
        private boolean signed;
        private int     mode;
        private String  className;
        private int     type;
        private String  typeName;
        private int     scale;
        private int     precision;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getNullable() {
            return nullable;
        }

        public void setNullable(int nullable) {
            this.nullable = nullable;
        }

        public boolean isSigned() {
            return signed;
        }

        public void setSigned(boolean signed) {
            this.signed = signed;
        }

        public int getMode() {
            return mode;
        }

        public void setMode(int mode) {
            this.mode = mode;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public int getScale() {
            return scale;
        }

        public void setScale(int scale) {
            this.scale = scale;
        }

        public int getPrecision() {
            return precision;
        }

        public void setPrecision(int precision) {
            this.precision = precision;
        }

    }
}
