package com.daigou.exception;

import com.daigou.common.BusinessException;
import com.daigou.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        String msg = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return Result.fail(400, msg);
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常: ", e);
        // 调试模式：返回详细错误信息，便于定位问题（生产环境请恢复为固定提示）
        String errorMsg = e.getClass().getSimpleName() + ": " + e.getMessage();
        if (e.getCause() != null) {
            errorMsg += " | Caused by: " + e.getCause().getClass().getSimpleName() + ": " + e.getCause().getMessage();
        }
        return Result.fail(500, "[DEBUG] " + errorMsg);
    }
}
