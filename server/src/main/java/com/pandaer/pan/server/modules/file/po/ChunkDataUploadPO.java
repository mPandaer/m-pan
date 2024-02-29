package com.pandaer.pan.server.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel("文件分片上传参数实体")
@Data
public class ChunkDataUploadPO {

    @ApiModelProperty("文件名")
    @NotBlank(message = "文件名字不能为空")
    private String filename;

    @ApiModelProperty("文件唯一标识")
    @NotBlank(message = "文件唯一标识不能为空")
    private String identifier;

    @ApiModelProperty("文件总大小")
    @NotNull(message = "文件总大小不能为空")
    private Long totalSize;

    @ApiModelProperty("文件分片总数")
    @NotNull(message = "文件分片总数不能为空")
    @Min(value = 1L,message = "文件分片总数不能低于1")
    private Integer totalChunks;

    @ApiModelProperty("当前分片号")
    @NotNull(message = "当前分片号不能为空")
    private Integer currentChunkNumber;

    @ApiModelProperty("当前分片大小")
    @NotNull(message = "当前分片大小不能为空")
    private Long currentChunkSize;

    @ApiModelProperty("当前分片数据")
    @NotNull(message = "当前分片文件数据不能为空")
    private MultipartFile fileData;
}
