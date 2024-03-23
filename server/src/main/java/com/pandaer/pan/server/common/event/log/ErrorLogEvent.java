//package com.pandaer.pan.server.common.event.log;
//
//import lombok.EqualsAndHashCode;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.ToString;
//import org.springframework.context.ApplicationEvent;
//
//@Getter
//@Setter
//@ToString
//@EqualsAndHashCode(callSuper = true)
//public class ErrorLogEvent extends ApplicationEvent {
//    private String errMsg;
//    private Long userId;
//
//
//    /**
//     * Create a new ApplicationEvent.
//     *
//     * @param source the object on which the event initially occurred (never {@code null})
//     */
//    public ErrorLogEvent(Object source,String message,Long userId) {
//        super(source);
//        this.errMsg = message;
//        this.userId = userId;
//    }
//}
