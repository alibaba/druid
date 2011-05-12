/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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

public enum SQLBinaryOperator {
    Add("+", 70), Subtract("-", 70), Multiply("*", 60), Divide("/", 60), Concat("||", 140), BitwiseAnd("&", 90),
    BitwiseNot("!", 130), BitwiseOr("|", 100), BitwiseXor("^", 50), BooleanAnd("AND", 140), BooleanOr("OR", 160),
    BooleanXor("XOR", 150), Equality("=", 170), Assignment(":=", 169), GreaterThan(">", 110), GreaterThanOrEqual(">=",
                                                                                                                 110),
    Is("IS", 110), LessThan("<", 110), LessThanOrEqual("<=", 110), LessThanOrEqualOrGreaterThan("<=>", 110),
    LessThanOrGreater("<>", 110), LeftShift("<<", 80), Like("LIKE", 110), RightShift(">>", 80), Modulus("%", 60),
    NotEqual("!=", 110), NotLessThan("!<", 110), NotGreaterThan("!>", 110), Union("UNION", 0),
    NotLike("NOT LIKE", 110), IsNot("IS NOT", 110), Escape("ESCAPE", 110), RegExp("REGEXP", 110),
    NotRegExp("NOT REGEXP", 110), COLLATE("COLLATE", 20);

    public static int getPriority(SQLBinaryOperator operator) {
        return 0;
    }

    // public static final int Add = 0;
    // public static final int Subtract = 26;
    // public static final int Multiply = 22;
    // public static final int Divide = 9;
    // public static final int Concat = 42;
    // public static final int Assign = 2;
    // public static final int BitwiseAnd = 3;
    // public static final int BitwiseNot = 4;
    // public static final int BitwiseOr = 5;
    // public static final int BitwiseXor = 6;
    // public static final int BooleanAnd = 7;
    // public static final int BooleanOr = 8;
    // public static final int Equality = 10;
    // public static final int GreaterThan = 11;
    // public static final int GreaterThanOrEqual = 12;
    // public static final int Is = 13;
    // public static final int LessThan = 14;
    // public static final int LessThanOrEqual = 15;
    // public static final int LessThanOrGreater = 16;
    // public static final int LeftShift = 17;
    // public static final int Like = 18;
    // public static final int RightShift = 19;
    // public static final int Modulus = 21;
    // public static final int NotEqual = 23;
    // public static final int NotLessThan = 24;
    // public static final int NotGreaterThan = 25;
    // public static final int Union = 27;
    // public static final int NotLike = 40;
    // public static final int IsNot = 41;
    // public static final int Escape = 43;
    //

    public final String name;
    public final int    priority;

    SQLBinaryOperator(){
        this(null, 0);
    }

    SQLBinaryOperator(String name, int priority){
        this.name = name;
        this.priority = priority;
    }
}
