package com.mood.notify;

import lombok.Data;

import java.util.List;

@Data
public class DingDingNotifyAT {
    private List<String> atMobiles;
    private String isAtAll;
}
