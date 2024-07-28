package com.keven.repeatsubmit.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoRepeatSubmit {

    /**
     * 间隔时间
     * @return
     */
    int interval() default 1000;

    /**
     * 错误消息
     * @return
     */
    String message() default "不允许重复提交";

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
