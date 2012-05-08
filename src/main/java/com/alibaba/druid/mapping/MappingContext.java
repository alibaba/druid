package com.alibaba.druid.mapping;

import java.util.ArrayList;
import java.util.List;

public class MappingContext {

    private List<Object> parameters;

    private boolean      generateAlias = false;

    public MappingContext(){
        this(new ArrayList<Object>());
    }

    public MappingContext(List<Object> parameters){
        this.parameters = parameters;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }

    public boolean isGenerateAlias() {
        return generateAlias;
    }

    public void setGenerateAlias(boolean generateAlias) {
        this.generateAlias = generateAlias;
    }

}
