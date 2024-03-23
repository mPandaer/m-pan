package com.pandaer.pan.server.common.stream.event.log;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@Data
public class ErrorLogEvent implements Serializable {
    private String errMsg;
    private Long userId;

    public ErrorLogEvent(String message,Long userId) {
        this.errMsg = message;
        this.userId = userId;
    }
}
