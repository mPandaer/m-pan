package com.pandaer.pan.server.modules.file.context;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Data
public class DeleteFileWithRecycleContext implements Serializable {

    private List<Long> fileIdList;

    private Long userId;
}
