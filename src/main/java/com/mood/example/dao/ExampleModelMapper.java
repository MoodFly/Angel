package com.mood.example.dao;

import com.mood.example.model.ExampleModel;

import java.util.List;
import java.util.Map;

public interface ExampleModelMapper {
    int insert(ExampleModel transferExamine);
    int update(ExampleModel transferExamine);
    List<ExampleModel> query(Map<String, Object> params);
    ExampleModel queryOne(Map<String, Object> params);
}
