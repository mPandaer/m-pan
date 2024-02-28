package com.pandaer.pan.server.common.event.file;

import lombok.*;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class DeleteFileWithRecycleEvent extends ApplicationEvent {

    private final List<Long> deleteFileIdList;


    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public DeleteFileWithRecycleEvent(Object source,List<Long> deleteFileIdList) {
        super(source);
        this.deleteFileIdList = deleteFileIdList;
    }
}
