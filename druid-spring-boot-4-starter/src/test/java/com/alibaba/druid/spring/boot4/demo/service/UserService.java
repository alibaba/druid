package com.alibaba.druid.spring.boot4.demo.service;

import com.alibaba.druid.spring.boot4.demo.model.User;

public interface UserService {
    User findById(Long id);
}
