package com.pandaer.pan.server.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@ApiModel("文件重命名参数实体")
@Data
public class UpdateFilenamePO implements Serializable {


    /**
     * 当前文件的加密ID
     */
    @ApiModelProperty("当前文件的加密ID")
    @NotBlank(message = "当前文件Id不能为空")
    private String fileId;

    /**
     * 新的文件名
     */
    @ApiModelProperty("新的文件名")
    @NotBlank(message = "新文件名不能为空")
    private String newFilename;
}
