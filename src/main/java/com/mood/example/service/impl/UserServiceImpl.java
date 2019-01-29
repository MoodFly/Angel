package com.mood.example.service.impl;

import com.mood.annotation.MoodDataSource;
import com.mood.base.User;
import com.mood.example.dao.ExampleModelMapper;
import com.mood.example.model.ExampleModel;
import com.mood.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.mood.angelenum.DataSourceType.READ;
import static com.mood.angelenum.DataSourceType.WRITE;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    ExampleModelMapper exampleModelMapper;
    @MoodDataSource(WRITE)
    @Override
    public int insert(User shop) {
        return 0;
    }


    @Override
    @MoodDataSource(WRITE)
    public int update(User shop) {
        return 0;
    }

    @Override
    @MoodDataSource(READ)
    public List<User> query(Map<String, Object> params) {
        return null;
    }

    @Override
    @MoodDataSource(READ)
    public User queryCount(Map<String, Object> params) {
        return null;
    }

    @Override
    @MoodDataSource(READ)
    public ExampleModel queryOne(Long params) {
        return exampleModelMapper.queryOne(params).get(0);
    }
}
