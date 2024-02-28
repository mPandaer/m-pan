package com.pandaer.pan.server.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@ApiModel("删除文件到回收站的参数实体")
@Data
public class DeleteFileWithRecyclePO implements Serializable {

    @NotEmpty(message = "文件ID不能为空")
    @ApiModelProperty("删除的文件ID集合")
    private List<String> fileIdList;
}
