package com.mood.base;

import com.mood.utils.DatabaseContextHolder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.Nullable;
/**
 * @author: by Mood
 * @date: 2011-01-14 11:11:11
 * @Description: 系统运行 动态数据源配置
 * @version: 1.0
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Nullable
    @Override
    protected Object determineCurrentLookupKey() {
        return DatabaseContextHolder.getDatabaseType();
    }
}
