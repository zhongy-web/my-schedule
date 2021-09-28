package com.zhongy.community.advice;

import entity.Result;
import entity.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class ControllerExceptionHandleAdvice {
    private final static Logger logger = LoggerFactory.getLogger(ControllerExceptionHandleAdvice.class);

    @ExceptionHandler
    public Result handler(HttpServletRequest req, HttpServletResponse res, Exception e) {
        logger.info("Restful Http请求发生异常...");
        if (e instanceof TokenException) {
            logger.error("代码00：" + e.getMessage(), e);
            return new Result(false, StatusCode.ACCESSERROR, "无权限！");
        } else {
            logger.error("代码99：" + e.getMessage(), e);
            return new Result(false, StatusCode.ERROR, "服务器代码发生异常,请联系管理员");
        }

    }
}
