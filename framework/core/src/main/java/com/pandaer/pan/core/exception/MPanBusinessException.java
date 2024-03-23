package com.pandaer.pan.core.exception;

import com.pandaer.pan.core.response.ResponseCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MPanBusinessException extends RuntimeException {
    private final Integer code;
    private final String message;

    public MPanBusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public MPanBusinessException(ResponseCode responseCode) {
        super(responseCode.getMsg());
        this.code = responseCode.getCode();
        this.message = responseCode.getMsg();
    }

    public MPanBusinessException(String message) {
        super(message);
        this.code = ResponseCode.ERROR_PARAM.getCode();
        this.message = message;
    }

    public MPanBusinessException() {
        super(ResponseCode.ERROR_PARAM.getMsg());
        this.code = ResponseCode.ERROR_PARAM.getCode();
        this.message = ResponseCode.ERROR_PARAM.getMsg();
    }

}
