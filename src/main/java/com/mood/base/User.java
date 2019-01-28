package com.mood.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author: by Mood
 * @date: 2011-01-14 11:11:11
 * @Description: 系统用户基类 用于所有操作。可自行拓展
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int id;
    private String name;
    private Set<String> role;
    private Set<String> permisscode;
}
