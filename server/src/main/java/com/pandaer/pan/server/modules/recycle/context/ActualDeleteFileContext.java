package com.pandaer.pan.server.modules.recycle.context;



import com.pandaer.pan.server.modules.file.domain.MPanUserFile;
import lombok.Data;

import java.util.List;

@Data
public class ActualDeleteFileContext {

    private List<Long> fileIdList;

    private Long userId;


    /**
     * 带嵌套的文件列表
     */
    private List<MPanUserFile> nestRecords;

    /**
     * 不带嵌套的文件列表
     */
    private List<MPanUserFile> allRecords;

}
