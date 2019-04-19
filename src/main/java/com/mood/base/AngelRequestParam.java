package com.mood.base;

import lombok.Data;

import java.util.List;
@Data
public class AngelRequestParam {
    private String uuid;
    private String code;
    private List<String> data;
}
