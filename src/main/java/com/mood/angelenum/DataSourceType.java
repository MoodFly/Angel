package com.mood.angelenum;
/**
 * @author: by Mood
 * @date: 2011-01-14 11:11:11
 * @Description: 数据源枚举类
 * @version: 1.0
 */

public enum DataSourceType {
    WRITE("write"),
    READ("read");
    public String value;
    DataSourceType(String value){
        this.value=value;
    }
    public String getValue() {
        return this.value;
    }
    public static String getValue(String value) {
        for (DataSourceType o : DataSourceType.values()) {
            if (o.getValue().equals(value)) {
                return o.getValue();
            }
        }
        return "";
    }
}
