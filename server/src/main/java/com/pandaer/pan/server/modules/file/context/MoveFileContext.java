package com.pandaer.pan.server.modules.file.context;

import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class MoveFileContext {

    private List<Long> fileIdList;

    private Long targetParentId;

    private Long userId;

    private List<MPanUserFile> needMoveFileList;
}
