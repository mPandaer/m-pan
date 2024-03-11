package com.pandaer.pan.server.modules.share.context;

import lombok.Data;

import java.io.Serializable;

@Data
public class QueryChildFileListContext implements Serializable {

    private Long parentId;

    private Long shareId;


}
