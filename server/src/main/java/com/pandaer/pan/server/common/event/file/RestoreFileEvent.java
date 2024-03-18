package com.pandaer.pan.server.common.event.file;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.util.List;


/**
 * 文件还原事件 -- 将回收站中的文件还原到之前的目录中
 */
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
