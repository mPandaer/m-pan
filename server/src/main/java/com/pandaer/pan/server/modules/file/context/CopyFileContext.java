package com.pandaer.pan.server.modules.file.context;

import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data

public class CopyFileContext {

    private List<Long> copyFileIdList;

    private Long targetParentId;

    private Long userId;

    private List<MPanUserFile> needCopyFileList;
}
