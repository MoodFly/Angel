package com.mood.aspect;
import com.mood.annotation.Idempotent;
import com.mood.base.AngelRequestParam;
import com.mood.utils.RedisUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletResponse;

/**
 * @author: by Mood
 * @date: 2019-04-19 10:46:06
 * @Description: 幂等注解切面
 * @version: 1.0
 */
@Aspect
@Component
public class IdempotentAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdempotentAspect.class) ;
    @Autowired
    private Jedis jedis;
    @Around("@annotation(idempotent)" )
    public void invokeMethod(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        Object resp = null;
        Class clazz=joinPoint.getTarget().getClass();
        String caller = getCaller(joinPoint);
        try{
            LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
            String [] paraNameArr=u.getParameterNames(clazz.getMethod(joinPoint.getSignature().getName(), AngelRequestParam.class));
            String requestKey =parseKey(idempotent.express(),paraNameArr,joinPoint.getArgs());
            LOGGER.info("Idempotent KEY:{} ",requestKey);
            resp = checkIdempotent(requestKey)? joinPoint.proceed():"已发生调用，禁止重复调用";
        }catch (Exception e){
            LOGGER.error("Idempotent create Error Info:{} ",e);
        }finally {
            directResponse(resp, caller);
        }
    }
    /**
     * 调用redis读取数据请求幂等key，如果存在，则禁止去调用业务方法
     * @param requestKey
     * @return
     */
    private Boolean checkIdempotent(String requestKey) {
        if (!RedisUtil.getInstance(jedis).getMonitorRequestKey(requestKey)){
            return RedisUtil.getInstance(jedis).getManagerRequestKey(requestKey);
        }
        return false;
    }

    /**
     * 幂等表达式 在当前版本boot无法自动解析。手动调用SpelExpressionParser去解析与一下。
     * @param express
     * @param paraNameArr
     * @param args
     * @return
     */
    private String parseKey(String express, String [] paraNameArr,Object[] args) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        for(int i=0;i<paraNameArr.length;i++){
            context.setVariable(paraNameArr[i], args[i]);
        }
        return new SpelExpressionParser().parseExpression(express).getValue(context,String.class);
    }

    /**
     * 直接返回结果,不调用业务方法
     * @param resp
     * @param caller
     * @return
     */
    private static void directResponse(Object resp, String caller) {
        try {
            HttpServletResponse httpServletResponse = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/json; charset=UTF-8");
            if (resp != null) {
                httpServletResponse.getWriter().write(resp.toString());
            } else {
                httpServletResponse.getWriter().write("resp is null" + caller);
            }
        } catch (Exception e) {
            LOGGER.error("ApiAspect.directResponse error", e);
        }
    }
    /**
     * 获取接口调用者
     * @param pjp
     * @return
     */
    public static String getCaller(ProceedingJoinPoint pjp) {
        // 获取简单类名
        String className = pjp.getSignature().getDeclaringTypeName();
        String simpleClassName = className.substring(className.lastIndexOf(".") + 1);
        // 获取方法名
        String methodName = pjp.getSignature().getName();
        return simpleClassName + "#"+ methodName;
    }

}
