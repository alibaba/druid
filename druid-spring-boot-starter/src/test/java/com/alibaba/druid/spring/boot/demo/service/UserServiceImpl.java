package com.alibaba.druid.spring.boot.demo.service;

import com.alibaba.druid.spring.boot.demo.dao.UserDao;
import com.alibaba.druid.spring.boot.demo.model.User;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public User findById(Long id) {
        Optional<User> result = userDao.findById(id);
        if (!result.isPresent()) {
            return null;
        }

        return result.get();
    }

}
