package com.mood.utils;

import com.mood.base.User;

/**
 * @author: by Mood
 * @date: 2011-01-14 11:11:11
 * @Description: 线程安全用户信息handle
 * @version: 1.0
 */
public class UserHandle {

    private static ThreadLocal<User> userHolder = new ThreadLocal<>();

    public static User getUser() {
        return userHolder.get();
    }

    public static void setUser(User user) {
        userHolder.set(user);
    }

    public static void removeUser(){
        userHolder.remove();
    }
}
