package com.alibaba.druid.spring.boot3.demo.configurer;

import com.alibaba.druid.spring.boot3.demo.dao.UserDao;
import com.alibaba.druid.spring.boot3.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import jakarta.annotation.PostConstruct;

@Configuration
@Profile("default")
public class InitConfigurer{
    @Autowired
    private UserDao userDao;

    @PostConstruct
    public  void  init(){
        for (int i = 1; i <= 100; i++) {
            userDao.save(new User("TEST-NAME-" + i));
        }
    }
}
