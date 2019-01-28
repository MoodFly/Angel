package com.mood.utils;

import com.mood.base.User;

import java.util.Set;
/**
 * @author: by Mood
 * @date: 2011-01-14 11:11:11
 * @Description: 校验用户权限
 * @version: 1.0
 */
public class CheckPermissionUtil {
    public static boolean checkPermission(Set<String> permissionCodeSet , User user){
        permissionCodeSet.retainAll(user.getPermisscode());
        if(permissionCodeSet.isEmpty()) {
            return false;
        }else {
            return true;
        }
    }
}
