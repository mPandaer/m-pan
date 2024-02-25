package com.pandaer.pan.core.response;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class Resp<T> implements Serializable {
    private final Integer code;
    private final String msg;
    private final T data;

    private Resp(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    @JsonIgnore
    @JSONField(serialize = false)
    public boolean isSuccess() {
        return this.code.equals(ResponseCode.SUCCESS.getCode());
    }

    public static Resp<Object> success() {
        return new Resp<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMsg(), null);
    }

    public static Resp<Object> success(String msg) {
        return new Resp<>(ResponseCode.SUCCESS.getCode(), msg, null);
    }

    public static <T> Resp<T> successAndData(T data) {
        return new Resp<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMsg(), data);
    }


    public static Resp<Object> error() {
        return new Resp<>(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getMsg(), null);
    }

    public static Resp<Object> error(String msg) {
        return new Resp<>(ResponseCode.ERROR.getCode(), msg, null);
    }


    public static Resp<Object> error(Integer code, String msg) {
        return new Resp<>(code, msg, null);
    }

    public static Resp<Object> error(ResponseCode responseCode) {
        return new Resp<>(responseCode.getCode(), responseCode.getMsg(), null);
    }

    public static <T> Resp<T> errorAndData(T data) {
        return new Resp<>(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getMsg(), data);
    }

    public static <T> Resp<T> errorAndData(ResponseCode responseCode, T data) {
        return new Resp<>(responseCode.getCode(), responseCode.getMsg(), data);
    }


}
