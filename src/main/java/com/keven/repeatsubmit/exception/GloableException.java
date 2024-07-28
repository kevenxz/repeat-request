package com.keven.repeatsubmit.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常拦截器
 */
@ControllerAdvice
@ResponseBody
public class GloableException {

    private static final Logger log = LoggerFactory.getLogger(GloableException.class);

    public GloableException() {
    }
    @ExceptionHandler(value = RuntimeException.class)
    public String handlerRuntimeException(RuntimeException e){
        log.error(e.getMessage(),e);
        return e.getMessage();
    }

}
