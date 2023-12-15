package com.alibaba.druid.spring.boot3.demo.service;


import com.alibaba.druid.spring.boot3.demo.model.User;

public interface UserService {
    User findById(Long id);
}
