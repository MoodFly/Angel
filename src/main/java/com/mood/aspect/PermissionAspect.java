package com.mood.aspect;

import com.mood.base.User;
import com.mood.constact.ApplicationCode;
import com.mood.annotation.Permission;
import com.mood.exception.AngelException;
import com.mood.utils.CheckPermissionUtil;
import com.mood.utils.UserHandle;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
/**
 * @author: by Mood
 * @date: 2011-01-14 11:11:11
 * @Description: 权限切面
 * @version: 1.0
 */
@Aspect
@Component
public class PermissionAspect {
    //config you scanpackage
    @Around(value = "execution(*  com.renrenche.mood.example..*.*(..))" )
    public Object invokeMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        final Set<String> permissionCodeSet =new HashSet<>(32);
        Class clazz = joinPoint.getTarget().getClass();
        if (!clazz.isAnnotationPresent(Permission.class)) {
            return joinPoint.proceed();
        }
        String classPermissionCode="";
        Annotation annotation = clazz.getAnnotation(Permission.class);
        if(annotation instanceof Permission) {
            Permission permAnnotation = (Permission)annotation;
            classPermissionCode = permAnnotation.Param();
        }
        if(!StringUtils.isEmpty(classPermissionCode)) {
            String []arrMethodAccess = classPermissionCode.split(",");
            permissionCodeSet.addAll(Arrays.asList(arrMethodAccess));
        }
        Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
        String methodPermissionCode = "";
        if (method.isAnnotationPresent(Permission.class)) {
            Permission permAnnotation = method.getAnnotation(Permission.class);
            methodPermissionCode = permAnnotation.Param();
        }
        if(!StringUtils.isEmpty(methodPermissionCode)) {
            String []arrMethodAccess = methodPermissionCode.split(",");
            permissionCodeSet.addAll(Arrays.asList(arrMethodAccess));
        }
        if(permissionCodeSet.isEmpty()) {
            return joinPoint.proceed();
        }
        User user= UserHandle.getUser();
        if (!CheckPermissionUtil.checkPermission(permissionCodeSet,user)){
            throw new AngelException(ApplicationCode.unPermission);
        }
        return joinPoint.proceed();//调用目标方法
    }
}
