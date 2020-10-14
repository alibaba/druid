package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleXmlColumnProperties extends OracleSQLObjectImpl {
    private SQLName column;
    private OracleXMLTypeStorage storage;

    private Boolean allowNonSchema;
    private Boolean allowAnySchema;

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, storage);
        }
        visitor.endVisit(this);
    }

    public SQLName getColumn() {
        return column;
    }

    public void setColumn(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.column = x;
    }

    public OracleXMLTypeStorage getStorage() {
        return storage;
    }

    public void setStorage(OracleXMLTypeStorage x) {
        if (x != null) {
            x.setParent(this);
        }
        this.storage = x;
    }

    public Boolean getAllowNonSchema() {
        return allowNonSchema;
    }

    public void setAllowNonSchema(Boolean allowNonSchema) {
        this.allowNonSchema = allowNonSchema;
    }

    public Boolean getAllowAnySchema() {
        return allowAnySchema;
    }

    public void setAllowAnySchema(Boolean allowAnySchema) {
        this.allowAnySchema = allowAnySchema;
    }

    public static class OracleXMLTypeStorage extends OracleSQLObjectImpl {
        private boolean secureFile;
        private boolean basicFile;

        private boolean clob;
        private boolean binaryXml;

        private OracleLobParameters lobParameters;

        @Override
        public void accept0(OracleASTVisitor visitor) {

        }

        public boolean isSecureFile() {
            return secureFile;
        }

        public void setSecureFile(boolean secureFile) {
            this.secureFile = secureFile;
        }

        public boolean isBasicFile() {
            return basicFile;
        }

        public void setBasicFile(boolean basicFile) {
            this.basicFile = basicFile;
        }

        public boolean isClob() {
            return clob;
        }

        public void setClob(boolean clob) {
            this.clob = clob;
        }

        public boolean isBinaryXml() {
            return binaryXml;
        }

        public void setBinaryXml(boolean binaryXml) {
            this.binaryXml = binaryXml;
        }

        public OracleLobParameters getLobParameters() {
            return lobParameters;
        }

        public void setLobParameters(OracleLobParameters x) {
            if (x != null) {
                x.setParent(this);
            }
            this.lobParameters = x;
        }
    }
}
