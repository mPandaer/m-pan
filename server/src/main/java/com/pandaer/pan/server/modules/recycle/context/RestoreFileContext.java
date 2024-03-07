package com.pandaer.pan.server.modules.recycle.context;



import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
import lombok.Data;

import java.util.List;

@Data
public class RestoreFileContext {

    private List<Long> fileIdList;

    private Long userId;

    private List<MPanUserFile> userFileList;
}
