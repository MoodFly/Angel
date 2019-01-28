package com.mood.base;

import com.mood.angelenum.DataSourceType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
/**
 * @author: by Mood
 * @date: 2011-01-14 11:11:11
 * @Description: 数据源配置
 * @version: 1.0
 */
@Configuration
public class DataSourceConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfiguration.class) ;


    @Value("${jdbc.type}")
    private Class<? extends DataSource> dataSourceType;

    @Bean(name="writeDataSource")
    @ConfigurationProperties(prefix="write.jdbc")
    @Primary
    public  DataSource masterDataSource(){
        DataSource masterDataSource = DataSourceBuilder.create().type(dataSourceType).build();
        LOGGER.info("========= WRITE :{} =======",masterDataSource);
        return masterDataSource;
    }

    @Bean(name="readDataSource")
    @ConfigurationProperties(prefix="read.jdbc")
    public DataSource slaveDataSource(){
        DataSource slaveDataSource = DataSourceBuilder.create().type(dataSourceType).build();
        LOGGER.info("========= READ :{} ========",slaveDataSource);
        return slaveDataSource;
    }
    /**
     * @Qualifier 根据名称进行注入，通常是在具有相同的多个类型的实力的一个注入（列如有多个DataSource 的类型）
     */
    @Bean
    public DynamicDataSource dataSource(@Qualifier("writeDataSource") DataSource writeDataSource,
                                        @Qualifier("readDataSource") DataSource readDataSource){
        Map<Object,Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceType.WRITE, writeDataSource);
        targetDataSources.put(DataSourceType.READ, readDataSource);
        DynamicDataSource dataSource = new DynamicDataSource();
        dataSource.setTargetDataSources(targetDataSources);
        dataSource.setDefaultTargetDataSource(readDataSource);
        return dataSource;
    }
    /**
     *
     * <descption> 根据数据源创建SqlSessionFactoryBean </descption>
     *
     * @param
     * @return
     * @throws Exception
     */
    @Bean
    public SqlSessionFactoryBean seqSessionFactory(DynamicDataSource ds) throws Exception{
        SqlSessionFactoryBean fb = new SqlSessionFactoryBean();
        fb.setDataSource(ds);
        return fb;
    }

    @Bean
    public DataSourceTransactionManager tracsactionManager(DynamicDataSource ds){
        return new DataSourceTransactionManager(ds);
    }
}
