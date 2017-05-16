package com.alibaba.druid.spring.boot.demo.dao;


import com.alibaba.druid.spring.boot.demo.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserDao extends CrudRepository<User, Long> {}

