package com.mood.example.service;

import com.mood.base.User;
import com.mood.example.model.ExampleModel;

import java.util.List;
import java.util.Map;

public interface UserService {
    int insert(User shop);
    int update(User shop);
    List<User> query(Map<String, Object> params);
    User queryCount(Map<String, Object> params);
    public ExampleModel queryOne(Long params);
}
