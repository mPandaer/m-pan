package com.pandaer.pan.server.common.stream.event.file;

import lombok.*;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Data
public class DeleteFileWithRecycleEvent implements Serializable {

    private List<Long> deleteFileIdList;


    public DeleteFileWithRecycleEvent(List<Long> deleteFileIdList) {
        this.deleteFileIdList = deleteFileIdList;
    }
}
