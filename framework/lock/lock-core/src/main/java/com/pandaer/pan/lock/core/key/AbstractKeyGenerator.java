package com.pandaer.pan.lock.core.key;

import cn.hutool.core.util.ArrayUtil;
import com.pandaer.pan.core.utils.SpElUtil;
import com.pandaer.pan.lock.core.LockContext;
import com.pandaer.pan.lock.core.annotation.Lock;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractKeyGenerator implements KeyGenerator {

    @Override
    public String generateKey(LockContext context) {
        Lock annotation = context.getAnnotation();
        String[] keys = annotation.keys();
        Map<String,String> kvMap = new HashMap<>();
        if (ArrayUtil.isNotEmpty(keys)) {
            for (String key : keys) {
                kvMap.put(key, SpElUtil.getStringValue(key,context.getClassName(),context.getMethodName(),
                        context.getClassType(),context.getMethod(),context.getArgs(),context.getParameterType()
                        ,context.getTarget()));
            }
        }
        return doGenerateKey(context, kvMap);
    }

    protected abstract String doGenerateKey(LockContext context, Map<String, String> kvMap);
}
