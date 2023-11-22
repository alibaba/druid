package com.alibaba.druid.spring.boot3.demo.service;

import com.alibaba.druid.spring.boot3.demo.dao.UserDao;
import com.alibaba.druid.spring.boot3.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
