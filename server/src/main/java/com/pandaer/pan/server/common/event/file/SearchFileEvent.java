package com.pandaer.pan.server.common.event.file;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator;
import org.springframework.context.ApplicationEvent;

@Setter
@Getter
@ToString
public class SearchFileEvent extends ApplicationEvent {
    private String keyword;

    private Long userId;


    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public SearchFileEvent(Object source,String keyword,Long userId) {
        super(source);
        this.keyword = keyword;
        this.userId = userId;
    }
}
