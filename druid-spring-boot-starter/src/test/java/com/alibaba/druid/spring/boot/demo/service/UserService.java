package com.alibaba.druid.spring.boot.demo.service;


import com.alibaba.druid.spring.boot.demo.model.User;

public interface UserService {
    User findById(Long id);
}
