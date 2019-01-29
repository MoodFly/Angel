package com.mood.example.dao;

import com.mood.example.model.ExampleModel;
import java.util.List;

public interface ExampleModelMapper{
    List<ExampleModel> queryOne(Long cpOrderId);
}
