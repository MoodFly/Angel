package com.mood.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mood.constact.ApplicationCode;
import com.mood.exception.AngelException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
/**
 * @author: by Mood
 * @date: 2019-01-31 18:48:23
 * @Description: HttpClient 工具包 支持定期回收http连接
 * @version: 1.0
 */
public class HttpClientUtil {

    private final static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
    private String DEFAULT_AGENT = "Angel-HttpClient-0.0.1";
    /**
     * 默认编码
     */
    public static final String DEFAULT_ENCODING = "UTF-8";
    /**
     * 默认最大请求超时时间10秒,指客户端从服务器读取数据的timeout，超出后会抛出SocketTimeOutException
     */
    public static final int DEFAULT_REQUEST_TIMEOUT = 4000;
    /**
     * 默认最大请求超时时间10秒, http clilent中从connetcion pool中获得一个connection的超时时间
     */
    public static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT = 1000;
    /**
     * 默认指客户端和服务器建立连接的timeout
     */
    public static final int DEFAULT_CONNECT_TIMEOUT = 3000;
    RequestConfig defaultRequestConfig= RequestConfig.custom()
            .setRedirectsEnabled(true)
            .setMaxRedirects(3)
            .setConnectionRequestTimeout(DEFAULT_CONNECTION_REQUEST_TIMEOUT)
            .setConnectTimeout(DEFAULT_CONNECT_TIMEOUT)
            .setSocketTimeout(DEFAULT_REQUEST_TIMEOUT)
            .build();
    /**
     * http连接池管理器
     */
    private PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;
    /**
     * http客户端
     */
    private CloseableHttpClient closeableHttpClient;
    /**
     * 定期回收过期httpclient线程池
     */
    private static final ConnectionMonitorWorker connectionMonitorWorker;
    private static final Executor executor=Executors.newFixedThreadPool(5);

    static {
        connectionMonitorWorker = new ConnectionMonitorWorker();
        executor.execute(connectionMonitorWorker);
    }

    private HttpClientUtil(){
        poolingHttpClientConnectionManager=new PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(50);
        poolingHttpClientConnectionManager.setMaxTotal(200);
        poolingHttpClientConnectionManager.setDefaultConnectionConfig(ConnectionConfig.custom().setCharset(Charset.forName(DEFAULT_ENCODING)).build());

        closeableHttpClient = HttpClients.custom()
                .setUserAgent(DEFAULT_AGENT)
                .setDefaultRequestConfig(defaultRequestConfig)
                .setConnectionManager(poolingHttpClientConnectionManager)
                .build();
        connectionMonitorWorker.addConnMgr(poolingHttpClientConnectionManager);
    }

    /**
     * httpclient GET 请求
     * @param url 请求路径
     * @param requestParam 请求参数
     * @param requestHeader 封装header
     * @param encoding 编码
     * @return
     */
    public String httpGetMethod(String url, Map<String,String> requestParam,Map<String,String> requestHeader,String encoding){
        String result=null;
        StringBuffer log=new StringBuffer();
        if (url==null&&url.equals("")){throw new AngelException(ApplicationCode.errorRequestUrl);}
        StringBuffer urlSend=new StringBuffer(url);
        log.append("请求地址:").append(url);
        List<NameValuePair> qparams = Lists.newArrayList();
        for (Map.Entry<String, String> entry : requestParam.entrySet()) {
            qparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        String queryParamStr = URLEncodedUtils.format(qparams, encoding);
        log.append("请求参数:").append(queryParamStr);
        urlSend.append(queryParamStr);
        HttpGet httpGet = new HttpGet(urlSend.toString());
        for (Map.Entry<String, String> entry : requestHeader.entrySet()) {
            httpGet.setHeader(entry.getKey(),entry.getValue());
        }
        log.append("header参数:").append(requestHeader);
        HttpResponse httpResponse =null;
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            httpResponse = closeableHttpClient.execute(httpGet);
            result= EntityUtils.toString(httpResponse.getEntity(),encoding);
            watch.stop();
            log.append("请求结果:").append(result).append("请求耗时:").append(watch.getTotalTimeMillis());
            if (result==null){
                throw new AngelException(ApplicationCode.errorRequestResult);
            }
            logger.info(log.toString());
        } catch (Exception e) {
            log.append("返回异常:").append(e.getMessage());
            e.printStackTrace();
            //todo 增加重试
            logger.error(log.toString());
        }
        return result;
    }
    public String httpPostMethod(String url, String bodyParam, String contentType, Map<String,String> requestParam,Map<String,String> requestHeader,String encoding){
        String result=null;
        StringBuffer log=new StringBuffer();
        if (url==null&&url.equals("")){throw new AngelException(ApplicationCode.errorRequestUrl);}
        StringBuffer urlSend=new StringBuffer(url);
        log.append("请求地址:").append(url);
        List<NameValuePair> qparams = Lists.newArrayList();
        for (Map.Entry<String, String> entry : requestParam.entrySet()) {
            qparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        String queryParamStr = URLEncodedUtils.format(qparams, encoding);
        log.append("请求参数:").append(queryParamStr);
        urlSend.append(queryParamStr);
        HttpPost httpPost = new HttpPost(urlSend.toString());
        for (Map.Entry<String, String> entry : requestHeader.entrySet()) {
            httpPost.setHeader(entry.getKey(),entry.getValue());
        }
        log.append("header参数:").append(requestHeader);
        StringEntity entity = new StringEntity(bodyParam, encoding);
        entity.setContentType(contentType);
        log.append("body参数:").append(bodyParam).append("ContentType:").append(contentType);
        httpPost.setEntity(entity);
        HttpResponse httpResponse =null;
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            httpResponse = closeableHttpClient.execute(httpPost);
            result = EntityUtils.toString(httpResponse.getEntity(), encoding);
            watch.stop();
            log.append("请求结果:").append(result).append("请求耗时:").append(watch.getTotalTimeMillis());
            if (result==null){
                throw new AngelException(ApplicationCode.errorRequestResult);
            }
            logger.info(log.toString());
        } catch (Exception e) {
            log.append("返回异常:").append(e.getMessage());
            e.printStackTrace();
            //todo 增加重试
            logger.error(log.toString());
        }
        return result;
    }

    public static HttpClientUtil getInstance(){
        return new HttpClientUtil();
    }
    private static class ConnectionMonitorWorker implements Runnable{
        private final Set<HttpClientConnectionManager> connMgrSet = Sets.newHashSet();
        private volatile boolean shutdown;
        public void addConnMgr(HttpClientConnectionManager connectionManager) {
            connMgrSet.add(connectionManager);
        }
        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(3000L);
                        for (HttpClientConnectionManager connMgr : connMgrSet) {
                            connMgr.closeExpiredConnections();
                            connMgr.closeIdleConnections(60, TimeUnit.MILLISECONDS);
                        }
                    }
                }
            } catch (InterruptedException ex) {
            }
        }
    }
}

