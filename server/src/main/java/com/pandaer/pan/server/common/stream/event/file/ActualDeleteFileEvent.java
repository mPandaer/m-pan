package com.pandaer.pan.server.common.stream.event.file;

import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
import lombok.*;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Data
public class ActualDeleteFileEvent implements Serializable {

    private List<MPanUserFile> allRecords;



    public ActualDeleteFileEvent(List<MPanUserFile> allrecords) {
        this.allRecords= allrecords;
    }
}
