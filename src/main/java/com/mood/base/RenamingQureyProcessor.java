package com.mood.base;

import com.mood.annotation.HumpToUnderline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RenamingQureyProcessor extends ServletModelAttributeMethodProcessor {

    public static final char UNDERLINE = '_';
    @Autowired
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;


    private final Map<Class<?>, Map<String, String>> replaceMap = new ConcurrentHashMap<>();

    public RenamingQureyProcessor(boolean annotationNotRequired) {
        super(annotationNotRequired);
    }

    @Override
    protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest nativeWebRequest) {
        Object target = binder.getTarget();
        Map<String, String> mapping = getFieldMapping(target.getClass());
        QueryParamDataBinder paramNameDataBinder = new QueryParamDataBinder(target, binder.getObjectName(), mapping);
        requestMappingHandlerAdapter.getWebBindingInitializer().initBinder(paramNameDataBinder, nativeWebRequest);
        super.bindRequestParameters(paramNameDataBinder, nativeWebRequest);
    }

    private Map<String, String> getFieldMapping(Class<?> targetClass) {
        if (targetClass == Object.class) {
            return Collections.emptyMap();
        }
        if (replaceMap.containsKey(targetClass)) {
            return replaceMap.get(targetClass);
        }
        Map<String, String> renameMap = new HashMap<>();
        Field[] fields = targetClass.getDeclaredFields();
        HumpToUnderline paramNameAnnotation = targetClass.getAnnotation(HumpToUnderline.class);
        if (paramNameAnnotation != null) {
            for (Field field : fields) {
                renameMap.put(camelToUnderline(field.getName()), field.getName());
            }
        }
        renameMap.putAll(getFieldMapping(targetClass.getSuperclass()));
        if (renameMap.isEmpty()) {
            renameMap = Collections.emptyMap();
        }
        replaceMap.put(targetClass, renameMap);
        return renameMap;
    }
    private   String camelToUnderline(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(UNDERLINE);
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }


}
