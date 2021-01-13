/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.mysql.param;

public class MySqlParameterizedOutputVisitorTest extends MySQLParameterizedTest {
    public void test_0() throws Exception {
        validate("SELECT * FROM T WHERE ID IN (?, ?, ?)", "SELECT *\nFROM T\nWHERE ID IN (?)");
        paramaterizeAST("SELECT * FROM T WHERE ID IN (?, ?, ?)", "SELECT *\nFROM T\nWHERE ID IN (?, ?, ?)");
    }

    public void test_1() throws Exception {
        validate("SELECT * FROM T WHERE ID = 5", "SELECT *\nFROM T\nWHERE ID = ?");
        paramaterizeAST("SELECT * FROM T WHERE ID = 5", "SELECT *\nFROM T\nWHERE ID = ?");
    }

    public void test_2() throws Exception {
        validate("SELECT * FROM T WHERE 1 = 0 AND ID = 5", "SELECT *\nFROM T\nWHERE 1 = 0\n\tAND ID = ?");
        paramaterizeAST("SELECT * FROM T WHERE 1 = 0 AND ID = 5", "SELECT *\nFROM T\nWHERE ? = ?\n\tAND ID = ?");
    }

    public void test_3() throws Exception {
        validate("SELECT * FROM T WHERE ID = ? OR ID = ?", "SELECT *\nFROM T\nWHERE ID = ?");
        validate("SELECT * FROM T WHERE A.ID = ? OR A.ID = ?", "SELECT *\nFROM T\nWHERE A.ID = ?");
        validate("SELECT * FROM T WHERE 1 = 0 OR a.id = ? OR a.id = ? OR a.id = ? OR a.id = ?",
                 "SELECT *\nFROM T\nWHERE 1 = 0\n\tOR a.id = ?");
        validateOracle("SELECT * FROM T WHERE 1 = 0 OR a.id = ? OR a.id = ? OR a.id = ? OR a.id = ?",
                       "SELECT *\nFROM T\nWHERE 1 = 0\n\tOR a.id = ?");
        validateOracle("SELECT * FROM T WHERE A.ID = ? OR A.ID = ?", "SELECT *\nFROM T\nWHERE A.ID = ?");
        validate("INSERT INTO T (F1, F2) VALUES(?, ?), (?, ?), (?, ?)", "INSERT INTO T (F1, F2)\nVALUES (?, ?)");
        validate("update net_device d, sys_user u set d.resp_user_id=u.id where d.resp_user_login_name=u.username and d.id in (42354)", //
                 "UPDATE net_device d, sys_user u\nSET d.resp_user_id = u.id\nWHERE d.resp_user_login_name = u.username\n\tAND d.id IN (?)");


        paramaterizeAST("SELECT * FROM T WHERE ID = ? OR ID = ?", "SELECT *\n" +
                "FROM T\n" +
                "WHERE ID = ?\n" +
                "\tOR ID = ?");

        paramaterizeAST("SELECT * FROM T WHERE A.ID = ? OR A.ID = ?", "SELECT *\n" +
                "FROM T\n" +
                "WHERE A.ID = ?\n" +
                "\tOR A.ID = ?");

        paramaterizeAST("SELECT * FROM T WHERE 1 = 0 OR a.id = ? OR a.id = ? OR a.id = ? OR a.id = ?",
                 "SELECT *\n" +
                         "FROM T\n" +
                         "WHERE ? = ?\n" +
                         "\tOR a.id = ?\n" +
                         "\tOR a.id = ?\n" +
                         "\tOR a.id = ?\n" +
                         "\tOR a.id = ?");

        paramaterizeAST("INSERT INTO T (F1, F2) VALUES(?, ?), (?, ?), (?, ?)", "INSERT INTO T (F1, F2)\n" +
                "VALUES (?, ?),\n" +
                "\t(?, ?),\n" +
                "\t(?, ?)");

        paramaterizeAST("update net_device d, sys_user u set d.resp_user_id=u.id where d.resp_user_login_name=u.username and d.id in (42354)", //
                 "UPDATE net_device d, sys_user u\nSET d.resp_user_id = u.id\nWHERE d.resp_user_login_name = u.username\n\tAND d.id IN (?)");
    }
}
