package com.alibaba.druid.sql.visitor;

import java.util.List;

public interface SQLEvalVisitor extends SQLASTVisitor {

    public static final String EVAL_VAR_INDEX = "eval.varIndex";

    public static final String EVAL_VALUE     = "eval.value";

    List<Object> getParameters();
    
    void setParameters(List<Object> parameters);

    int incrementAndGetVariantIndex();
    
    boolean isMarkVariantIndex();
    
    void setMarkVariantIndex(boolean markVariantIndex);
}
