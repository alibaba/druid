/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.ast.expr;

/**
 * 
 * 二元操作符
 * @author wenshao 2011-5-20 下午12:32:02
 * @formatter:off
 */
public enum SQLBinaryOperator {
    Union("UNION", 0), 
    COLLATE("COLLATE", 20),
    BitwiseXor("^", 50), 
    
    Multiply("*", 60), 
    Divide("/", 60), 
    Modulus("%", 60), 
    
    Add("+", 70), 
    Subtract("-", 70), 
    
    LeftShift("<<", 80), 
    RightShift(">>", 80), 

    BitwiseAnd("&", 90), 
    BitwiseOr("|", 100),
    
    GreaterThan(">", 110), 
    GreaterThanOrEqual(">=", 110), 
    Is("IS", 110), 
    LessThan("<", 110), 
    LessThanOrEqual("<=", 110), 
    LessThanOrEqualOrGreaterThan("<=>",110),
    LessThanOrGreater("<>", 110), 
    
    Like("LIKE", 110),
    NotLike("NOT LIKE", 110), 
    
    RLike("RLIKE", 110),
    NotRLike("NOT RLIKE", 110),
    
    NotEqual("!=", 110), 
    NotLessThan("!<", 110),
    NotGreaterThan("!>", 110), 
    IsNot("IS NOT", 110), 
    Escape("ESCAPE", 110), 
    RegExp("REGEXP", 110),
    NotRegExp("NOT REGEXP", 110), 
    Equality("=", 110),
    
    BitwiseNot("!", 130), 
    Concat("||", 140), 
    
    BooleanAnd("AND", 140), 
    BooleanXor("XOR", 150), 
    BooleanOr("OR", 160), 
    Assignment(":=", 169)    
    ;

    public static int getPriority(SQLBinaryOperator operator) {
        return 0;
    }

    public final String name;
    public final int    priority;

    SQLBinaryOperator(){
        this(null, 0);
    }

    SQLBinaryOperator(String name, int priority){
        this.name = name;
        this.priority = priority;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getPriority() {
        return this.priority;
    }
    
    public boolean isRelational() {
        switch (this) {
            case Equality:
            case Like:
            case NotEqual:
            case GreaterThan:
            case GreaterThanOrEqual:
            case LessThan:
            case LessThanOrEqual:
            case LessThanOrGreater:
            case NotLike:
            case NotLessThan:
            case NotGreaterThan:
            case RLike:
            case NotRLike:
            case RegExp:
            case NotRegExp:
                return true;
            default:
                return false;
        }
    }
    
    public boolean isLogical() {
        return this == BooleanAnd || this == BooleanOr || this == BooleanXor;
    }
}
