package com.pandaer.pan.server.common.event.file;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class RestoreFileEvent extends ApplicationEvent {

    private final List<Long> restoreFileIdList;

    public RestoreFileEvent(Object source, List<Long> restoreFileIdList) {
        super(source);
        this.restoreFileIdList = restoreFileIdList;
    }
}
