package com.pandaer.pan.server.common.stream.event.file;

import lombok.*;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;
import java.util.List;


/**
 * 文件还原事件 -- 将回收站中的文件还原到之前的目录中
 */
@NoArgsConstructor
@Data
public class RestoreFileEvent implements Serializable {

    private  List<Long> restoreFileIdList;

    public RestoreFileEvent(List<Long> restoreFileIdList) {
        this.restoreFileIdList = restoreFileIdList;
    }
}
