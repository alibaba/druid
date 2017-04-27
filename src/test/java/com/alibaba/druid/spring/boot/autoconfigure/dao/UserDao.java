package com.alibaba.druid.spring.boot.autoconfigure.dao;


import com.alibaba.druid.spring.boot.autoconfigure.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserDao extends CrudRepository<User, Long> {}

