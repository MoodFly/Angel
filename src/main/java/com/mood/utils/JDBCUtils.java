package com.mood.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mood.applicationcode.AngelConstants;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * @Code
 * <p>
 *     try{
 *         JDBCUtils.getInstance().queryData(tableName,  column,  whereCondition,  limit,  offset,  size);
 *         JDBCUtils.getInstance().updateData(tableName,  column, data, whereCondition);
 *     }cache(Exception e){
 *         ...
 *     }finally{
 *         JDBCUtils.getInstance().close();
 *     }
 *
 * </p>
 * @author: by Mood
 * @date: 2019-04-23 17:21:29
 * @Description: JDBC工具包，针对数据修复工作，
 * @version: 1.0
 */
public class JDBCUtils {
    private volatile static JDBCUtils jdbcUtils=null;
    private final static Logger logger = LoggerFactory.getLogger(JDBCUtils.class);
    private static final String    CLASSPATH_URL_PREFIX = "classpath:";
    private static final String    JDBC = "jdbc:";
    private CountDownLatch countDownLatch=null;
    private Connection connection=null;
    ExecutorService executor = new ThreadPoolExecutor(5, 200, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1024), new ThreadFactoryBuilder().setNameFormat("JDBCUtils-WORKER-POOL-%d").build(), new ThreadPoolExecutor.AbortPolicy());
    private Connection createConnection(Properties properties) {
        String driver = String.valueOf(getProperty(properties, AngelConstants.ANGEL_HOST));
        //useAffectedRows=true 设置连接参数具体的更新的数目而不是match的行数。默认false为match到的行数。
        String url = JDBC+String.valueOf(getProperty(properties, AngelConstants.ANGEL_DBTYPE))+"://"+String.valueOf(getProperty(properties, AngelConstants.ANGEL_HOST))+":" +
                ""+String.valueOf(getProperty(properties, AngelConstants.ANGEL_PORT))+"/" +
                ""+String.valueOf(getProperty(properties, AngelConstants.ANGEL_DB))+"?useAffectedRows=true" ;
        String username = String.valueOf(getProperty(properties, AngelConstants.ANGEL_USER));
        String password = String.valueOf(getProperty(properties, AngelConstants.ANGEL_PASSWORD));
        try {
            Class.forName(driver);
            connection = (Connection) DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            logger.error("数据库驱动加载失败",e.getMessage());
        } catch (SQLException e) {
            logger.error("数据库建立连接失败",e.getMessage());
        }
        return connection;
    }
    private JDBCUtils(){
    }

    /**
     * 创建JDBC连接 长链接 一般用于线上数据修复是进行拉取数据使用，由于有不同的公司数据库代理中间件，可供活跃连接数有限制，禁止生产环境使用。
     * 使用完毕需要释放连接，用为长连接会占用数据库的内存，会对线上数据造成影响
     * 默认读取classpath下的配置文件angel.properties，可以启动参数传入
     * @return
     * @throws IOException
     */
    public static JDBCUtils getInstance() throws IOException {
        if (jdbcUtils!=null){
            synchronized (JDBCUtils.class){
                if (jdbcUtils!=null){
                    logger.info("## load canal configurations");
                    String conf = System.getProperty("angel.conf", "classpath:angel.properties");
                    Properties properties = new Properties();
                    if (conf.startsWith(CLASSPATH_URL_PREFIX)) {
                        conf = StringUtils.substringAfter(conf, CLASSPATH_URL_PREFIX);
                        properties.load(JDBCUtils.class.getClassLoader().getResourceAsStream(conf));
                    } else {
                        properties.load(new FileInputStream(conf));
                    }
                    jdbcUtils=new JDBCUtils();
                    jdbcUtils.createConnection(properties);
                    return jdbcUtils;
                }
            }
        }
        return jdbcUtils;
    }

    /**
     * 读取配置项
     * @param properties
     * @param key
     * @return
     */
    private static String getProperty(Properties properties, String key) {
        key = StringUtils.trim(key);
        String value = System.getProperty(key);
        if (value == null) {
            value = System.getenv(key);
        }
        if (value == null) {
            value = properties.getProperty(key);
        }
        return StringUtils.trim(value);
    }

    /**
     * 关闭数据库连接 抛出关闭异常，调用处需要处理
     */
    private  void close() throws SQLException {
        if(jdbcUtils!=null){
            if (executor!=null){
                executor.shutdown();
                if (connection!=null){
                    connection.close();
                }
            }
        }
    }

    /**
     * 查询数据
     * @param tableName 表名
     * @param column 查询数据列
     * @param whereCondition 条件语句
     * @param limit 是否分页 当limit传入""的时候，不使用mysql特有的分页关键字，为标准SQL，支出其他数据库
     * @param offset 分页偏移量
     * @param size 每页数据量
     * @return
     */

    private ResultSet queryData(String tableName, String column, String whereCondition, String limit, int offset, int size) {
        String sql ="";
        if (whereCondition.equals("")&&!limit.equals("")){
            sql="select "+column+" from "+ tableName + " limit "+ offset +", " +size ;
        }else if(!whereCondition.equals("")&&!limit.equals("")){
            sql="select "+column+" from "+ tableName +" where " + whereCondition +" limit "+ offset +", " +size ;
        }else if(whereCondition.equals("")&&limit.equals("")){
            sql="select "+column+" from "+ tableName +" where " + whereCondition ;
        }
        ResultSet rs =null;
        PreparedStatement pstmt;
        if ("".equals(sql)){
            return rs;
        }
        try {
            pstmt = (PreparedStatement)connection.prepareStatement(sql);
            rs = pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    /**
     *
     * @param tableName 数据表
     * @param column 数据列
     * @param data 数据
     * @param whereCondition 条件
     * @return
     */
    private int updateData(String tableName, String column,String data,String whereCondition) {
        int i = 0;
        String sql = "update "+tableName+" set "+column +"='"+data+"'  where " + whereCondition;
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) connection.prepareStatement(sql);
            i = pstmt.executeUpdate();
            if (0==i){
                logger.error("更新失败");
            }else {
                logger.info("更新成功" );
            }
            pstmt.close();
        } catch (SQLException e) {
            logger.error("更新数据库失败:"+e.getMessage());
        }
        return i;
    }

    /**
     * 分片提交任务到线程池，不需要汇总任务
     * @param pageTotal
     * @param runnable
     */
    private void commitJob(int pageTotal,int pageSize,Function runnable)throws Exception{
         countDownLatch=new CountDownLatch(pageTotal);
            for (int i=0;i<pageTotal;i++){
                final int m=i*pageSize;
                executor.execute(()-> {
                    try {
                       runnable.apply(m);
                    } catch (Exception e) {
                    }finally {
                        countDownLatch.countDown();
                    }
                });
            }
            countDownLatch.await();
    }
    /**
     * 分片提交任务到线程池，汇总任务
     * @param pageTotal
     * @param runnable
     */
    private Object submitJob(int pageTotal,int pageSize,Function runnable)throws Exception{
        countDownLatch =new CountDownLatch(pageTotal);
        List reslts= Lists.newArrayList();
            for (int i=0;i<pageTotal;i++){
                final int m=i*pageSize;
                Future future=executor.submit(()-> {
                    try {
                        runnable.apply(m);
                    } catch (Exception e) {
                    }finally {
                        countDownLatch.countDown();
                    }
                });
                reslts.add(future.get());
            }
            countDownLatch.await();
         return reslts;
    }
}
