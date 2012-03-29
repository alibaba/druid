package com.alibaba.druid.spring;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

public class UserDao extends SqlMapClientDaoSupport implements IUserDao {

    public void addUser(User user) {
        getSqlMapClientTemplate().insert("User.insert", user);
    }
}
