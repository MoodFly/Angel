package com.mood.base;

import lombok.Data;

import java.io.Serializable;

@Data
public class ElasticsearchQueryParam implements Serializable {
    private int from;
    private int size;
    private String match;
}
