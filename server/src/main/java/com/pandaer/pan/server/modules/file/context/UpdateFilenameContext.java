package com.pandaer.pan.server.modules.file.context;

import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;


@Data
public class UpdateFilenameContext implements Serializable {

    /**
     * 当前文件的ID
     */
    private Long fileId;

    /**
     * 新的文件名
     */
    private String newFilename;

    private Long userId;

    private MPanUserFile entity;
}
