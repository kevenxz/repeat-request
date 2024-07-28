package com.keven.repeatsubmit.aop;

import cn.hutool.core.util.StrUtil;
import com.keven.repeatsubmit.annotation.NoRepeatSubmit;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.util.RequestUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@AllArgsConstructor
@Slf4j
public class RepeatSubmitAspect {


    private RedissonClient redissonClient;

    // Key名字
    private static final String KEY_PREFIX = "repeat:noSubmit:";


    @Pointcut("@annotation(noRepeatSubmit)")
    public void pointcut(NoRepeatSubmit noRepeatSubmit)  {
    }


    @Before(value = "pointcut(repeatSubmit)", argNames = "joinPoint,repeatSubmit")
    public void before(JoinPoint joinPoint, NoRepeatSubmit repeatSubmit) {
        // 获取当前请求
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Assert.notNull(requestAttributes, "requestAttributes 不能为空");
        HttpServletRequest request = requestAttributes.getRequest();

        String remoteAddr = request.getRemoteAddr();
        String authorization = request.getHeader("Authorization");
        String servletPath = request.getServletPath();

        log.info("remoteAddr: {}, authorization: {}, servletPath: {}", remoteAddr, authorization, servletPath);


        // 获取注解的参数
        String message = repeatSubmit.message();
        int interval = repeatSubmit.interval();
        TimeUnit timeUnit = repeatSubmit.timeUnit();
        log.info("仿重的参数 message: {}, interval: {}", message, interval);
        // 获取 限时锁

        RLock lock = redissonClient.getLock(getKey(authorization, servletPath, remoteAddr));
        log.info("获取锁 {}", lock.getName());
        try {
            boolean isLock = lock.tryLock(0, interval, timeUnit);
            if (isLock) {
                return;
            }
            throw new RuntimeException(message);
        } catch (InterruptedException e) {
            throw new RuntimeException("获取锁失败",e);
        }



    }

    private String getKey(String authorization, String url, String remoteAddr) {
        if (StrUtil.isBlank(authorization)) {
            return KEY_PREFIX + url + ":" + remoteAddr;
        }
        return KEY_PREFIX + authorization + ":" + url + ":" + remoteAddr;
    }

}
