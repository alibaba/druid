package com.alibaba.druid.spring.boot.autoconfigure.service;


import com.alibaba.druid.spring.boot.autoconfigure.model.User;

public interface UserService {
    User findById(Long id);
}
