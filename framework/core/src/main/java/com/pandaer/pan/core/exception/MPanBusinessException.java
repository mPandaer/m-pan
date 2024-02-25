package com.pandaer.pan.core.exception;

import com.pandaer.pan.core.response.ResponseCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MPanBusinessException extends RuntimeException {
    private final Integer code;
    private final String msg;

    public MPanBusinessException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public MPanBusinessException(ResponseCode responseCode) {
        super(responseCode.getMsg());
        this.code = responseCode.getCode();
        this.msg = responseCode.getMsg();
    }

    public MPanBusinessException(String msg) {
        super(msg);
        this.code = ResponseCode.ERROR_PARAM.getCode();
        this.msg = msg;
    }

    public MPanBusinessException() {
        super(ResponseCode.ERROR_PARAM.getMsg());
        this.code = ResponseCode.ERROR_PARAM.getCode();
        this.msg = ResponseCode.ERROR_PARAM.getMsg();
    }

}
