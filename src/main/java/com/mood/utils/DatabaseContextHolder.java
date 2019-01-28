package com.mood.utils;


import com.mood.angelenum.DataSourceType;

public class DatabaseContextHolder {
    private static final ThreadLocal<DataSourceType> contextHolder = new ThreadLocal<DataSourceType>();
    public static void setDatabaseType(DataSourceType type){
        if(type ==null) throw new NullPointerException();
        contextHolder.set(type);
    }
    public static DataSourceType getDatabaseType(){
        return contextHolder.get() ==null ?DataSourceType.READ:contextHolder.get();
    }
    public static void clear(){
        contextHolder.remove();
    }
}
