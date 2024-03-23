package com.pandaer.pan.server.common.stream.event.file;

import lombok.*;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;

@NoArgsConstructor
@Data
public class SearchFileEvent implements Serializable {
    private String keyword;

    private Long userId;

    public SearchFileEvent(String keyword,Long userId) {
        this.keyword = keyword;
        this.userId = userId;
    }
}
