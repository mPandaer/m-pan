package com.pandaer.pan.server.modules.log.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pandaer.pan.server.modules.log.domain.MPanErrorLog;
import com.pandaer.pan.server.modules.log.service.IErrorLogService;
import com.pandaer.pan.server.modules.log.mapper.MPanErrorLogMapper;
import org.springframework.stereotype.Service;

/**
* @author pandaer
* @description 针对表【m_pan_error_log(错误日志表)】的数据库操作Service实现
* @createDate 2024-02-25 18:38:39
*/
@Service
public class ErrorLogServiceImpl extends ServiceImpl<MPanErrorLogMapper, MPanErrorLog>
    implements IErrorLogService {

}




