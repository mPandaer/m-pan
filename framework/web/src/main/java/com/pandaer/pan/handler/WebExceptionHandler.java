package com.pandaer.pan.handler;

import com.pandaer.pan.core.exception.MPanBusinessException;
import com.pandaer.pan.core.response.Resp;
import com.pandaer.pan.core.response.ResponseCode;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class WebExceptionHandler {
    @ExceptionHandler(value = MPanBusinessException.class)
    public Resp<Object> rPanBusinessExceptionHandler(MPanBusinessException e) {
        return Resp.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Resp<Object> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        ObjectError objectError = e.getBindingResult().getAllErrors().stream().findFirst().get();
        return Resp.error(ResponseCode.ERROR_PARAM.getCode(), objectError.getDefaultMessage());
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public Resp<Object> constraintDeclarationExceptionHandler(ConstraintViolationException e) {
        ConstraintViolation<?> constraintViolation = e.getConstraintViolations().stream().findFirst().get();
        return Resp.error(ResponseCode.ERROR_PARAM.getCode(), constraintViolation.getMessage());
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public Resp<Object> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e) {
        return Resp.error(ResponseCode.ERROR_PARAM);
    }

    @ExceptionHandler(value = IllegalStateException.class)
    public Resp<Object> illegalStateExceptionHandler(IllegalStateException e) {
        return Resp.error(ResponseCode.ERROR_PARAM);
    }

    @ExceptionHandler(value = BindException.class)
    public Resp<Object> bindExceptionHandler(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldErrors().stream().findFirst().get();
        return Resp.error(ResponseCode.ERROR_PARAM.getCode(), fieldError.getDefaultMessage());
    }
    
    @ExceptionHandler(value = RuntimeException.class)
    public Resp<Object> runtimeExceptionHandler(RuntimeException e) {
        return Resp.error(ResponseCode.ERROR.getCode(), e.getMessage());
    }
}
