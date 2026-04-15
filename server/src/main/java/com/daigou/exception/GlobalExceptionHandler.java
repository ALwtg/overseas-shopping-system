package com.daigou.exception;

import com.daigou.common.BusinessException;
import com.daigou.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;

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
        // 输出详细堆栈到服务器日志
        log.error("系统异常: ", e);
        
        // 获取完整堆栈信息
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        
        // 构建详细错误信息
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("异常类型: ").append(e.getClass().getName()).append("\n");
        errorMsg.append("异常信息: ").append(e.getMessage()).append("\n");
        errorMsg.append("堆栈跟踪: \n").append(stackTrace);
        
        if (e.getCause() != null) {
            errorMsg.append("\n根因异常: ").append(e.getCause().getClass().getName()).append("\n");
            errorMsg.append("根因信息: ").append(e.getCause().getMessage());
        }
        
        return Result.fail(500, errorMsg.toString());
    }
}
