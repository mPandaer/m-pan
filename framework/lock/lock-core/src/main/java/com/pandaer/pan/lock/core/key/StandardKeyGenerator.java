package com.pandaer.pan.lock.core.key;

import com.pandaer.pan.lock.core.LockContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class StandardKeyGenerator extends AbstractKeyGenerator{

    /**
     * 默认生成key的格式
     * className:methodName:parameterType1:...:parameterTypeN:value1:...:valueN
     * @param context
     * @param kvMap
     * @return
     */
    @Override
    protected String doGenerateKey(LockContext context, Map<String, String> kvMap) {
        List<String> keyList = new ArrayList<>();
        keyList.add(context.getClassName());
        keyList.add(context.getMethodName());
        for (Class<?> parameterType : context.getParameterType()) {
            keyList.add(parameterType.getName());
        }
        keyList.addAll(kvMap.values());
        return String.join(":", keyList);
    }
}
