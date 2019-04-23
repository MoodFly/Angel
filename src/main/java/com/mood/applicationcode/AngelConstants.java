package com.mood.applicationcode;

import lombok.Data;

/**
 * @author: by Mood
 * @date: 2011-01-14 11:11:11
 * @Description: 系统使用常量
 * @version: 1.0
 */
@Data
public class AngelConstants {
    public static final String ROOT                              = "angel";
    public static final String JDBCUTILS                         = "jdbcutils";
    public static final String ANGEL_DRIVER                      = ROOT + "."+JDBCUTILS+ "." + "driver";
    public static final String ANGEL_DBTYPE                      = ROOT + "."+JDBCUTILS+ "." + "type";
    public static final String ANGEL_HOST                        = ROOT + "."+JDBCUTILS+ "." + "host";
    public static final String ANGEL_PORT                        = ROOT + "."+JDBCUTILS+ "." + "port";
    public static final String ANGEL_DB                          = ROOT + "."+JDBCUTILS+ "." + "db";
    public static final String ANGEL_USER                        = ROOT + "."+JDBCUTILS+ "." + "user";
    public static final String ANGEL_PASSWORD                    = ROOT + "."+JDBCUTILS+ "." + "password";
}
