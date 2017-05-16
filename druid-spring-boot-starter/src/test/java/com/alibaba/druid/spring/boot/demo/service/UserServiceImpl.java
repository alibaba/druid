package com.alibaba.druid.spring.boot.demo.service;


import com.alibaba.druid.spring.boot.demo.dao.UserDao;
import com.alibaba.druid.spring.boot.demo.model.User;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService,InitializingBean {
    @Autowired
    private UserDao userDao;

    @Override
    public User findById(Long id) {
        return userDao.findOne(id);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (int i = 1; i <= 100; i++) {
            userDao.save(new User("TEST-NAME-" + i));
        }
    }
}
