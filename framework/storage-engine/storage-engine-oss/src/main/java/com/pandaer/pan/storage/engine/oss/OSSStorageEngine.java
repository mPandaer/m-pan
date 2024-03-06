package com.pandaer.pan.storage.engine.oss;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import com.aliyuncs.utils.StringUtils;
import com.pandaer.pan.core.constants.MPanConstants;
import com.pandaer.pan.core.exception.MPanFrameworkException;
import com.pandaer.pan.core.utils.FileUtil;
import com.pandaer.pan.core.utils.UUIDUtil;
import com.pandaer.pan.storage.engine.core.AbstractStorageEngine;
import com.pandaer.pan.storage.engine.core.context.*;
import com.pandaer.pan.storage.engine.oss.config.OSSStorageEngineConfig;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 阿里云OSS存储引擎
 */
@Log4j2
@Component
public class OSSStorageEngine extends AbstractStorageEngine {

    private static final Integer TEN_THOUSAND_NUM = 10000;
    private static final String CACHE_KEY_TEMPLATE = "oss_cache_upload_id_%s_%s";
    private static final String IDENTIFIER_KEY = "identifier";
    private static final String USER_ID_KEY = "userId";
    private static final String UPLOAD_ID_KEY = "uploadId";
    private static final String E_TAG_KEY = "eTag";
    private static final String PART_NUMBER_KEY = "partNumber";
    private static final String PART_SIZE_KEY = "partSize";
    private static final String PART_CRC_KEY = "partCRC";
    @Autowired
    private OSSStorageEngineConfig config;

    @Autowired
    private OSSClient ossClient;

    /**
     * 单文件上传
     *
     * @param storeFileContext
     * @throws IOException
     */
    @Override
    protected void doStoreFile(StoreFileContext storeFileContext) throws IOException {
        String realPath = getFilePath(FileUtil.getFileSuffix(storeFileContext.getFilename()));
        ossClient.putObject(config.getBucketName(), realPath, storeFileContext.getInputStream());
        storeFileContext.setRealPath(realPath);
    }


    @Override
    protected void doDeleteFile(DeleteFileContext deleteFileContext) throws IOException {
        deleteFileContext.getRealFilePathList().forEach(realPath -> {
            if (realPath.contains("?")) {
                Map<String, String> paramMap = getParamMap(realPath);
                if (paramMap == null) {
                    return;
                }
                String uploadId = paramMap.get(UPLOAD_ID_KEY);
                String identifier = paramMap.get(IDENTIFIER_KEY);
                Long userId = Long.parseLong(paramMap.get(USER_ID_KEY));
                String cacheKey = getCacheKey(identifier, userId);
                getCache().evict(cacheKey);
                try {
                    ossClient.abortMultipartUpload(new AbortMultipartUploadRequest(config.getBucketName(), getBaseUrl(realPath), uploadId));
                } catch (OSSException | ClientException e) {
                    log.info("删除文件失败,realPath:{}", realPath, e);
                }
            }else {
                ossClient.deleteObject(config.getBucketName(), realPath);
            }
        });
    }

    /**
     * 上传文件分片
     * 具体的业务逻辑
     * 1. 创建一个文件上传的事件
     * 2. 上传文件分片
     * 3. 所有分片上传完成后，合并分片
     * 注意点：
     * 1. 初始化文件上传事件的线程安全
     * 2. 分片分片上传是需要带有一个全局唯一的uploadId,让所有上传线程共享这个uploadId
     * 3. 我们还要保证，每个分片上传线程都有能力取消分片上传
     *
     * todo 暴力线程安全 后期优化
     * @param context
     * @throws IOException
     */
    @Override
    protected synchronized void doStoreChunkFile(StoreFileChunkContext context) throws IOException {
        if (context.getTotalChunks() > TEN_THOUSAND_NUM) {
            throw new MPanFrameworkException("分片数量不能超过10000");
        }
        String cacheKey = getCacheKey(context.getIdentifier(),context.getUserId());
        ChunkUploadEntity entity = getCache().get(cacheKey, ChunkUploadEntity.class);
        if (entity == null) {
            entity = initChunkUploadInfo(context.getFilename(),cacheKey);
        }

        UploadPartRequest request = new UploadPartRequest();
        request.setBucketName(config.getBucketName());
        request.setKey(entity.getRealPath());
        request.setUploadId(entity.getUploadId());
        request.setInputStream(context.getInputStream());
        request.setPartSize(context.getCurrentChunkSize());
        request.setPartNumber(context.getCurrentChunkNumber());
        UploadPartResult result = ossClient.uploadPart(request);
        System.out.println(result);
        if (result == null) {
            throw new MPanFrameworkException("分片上传失败");
        }

        PartETag partETag = result.getPartETag();


        //拼装文件分片的URL;
        JSONObject params = new JSONObject();
        params.put(IDENTIFIER_KEY, context.getIdentifier());
        params.put(USER_ID_KEY, context.getUserId());
        params.put(UPLOAD_ID_KEY, entity.getUploadId());
        params.put(E_TAG_KEY, partETag.getETag());
        params.put(PART_NUMBER_KEY, partETag.getPartNumber());
        params.put(PART_SIZE_KEY,partETag.getPartSize());
        params.put(PART_CRC_KEY,partETag.getPartCRC());
        String realPath = assembleUrl(entity.getRealPath(),params);
        System.out.println(realPath);
        context.setRealPath(realPath);

    }

    private String assembleUrl(String realPath, JSONObject params) {
        if (params == null || params.isEmpty()) {
            return realPath;
        }
        StringBuffer urlStringBuffer = new StringBuffer(realPath);
        urlStringBuffer.append("?");
        params.forEach((k, v) ->
            urlStringBuffer.append(k).append("=").append(v).append("&"));
        return urlStringBuffer.substring(0, urlStringBuffer.length() - 1);
    }

    private String getBaseUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            return url;
        }
        int index = url.indexOf("?");
        if (index == -1) {
            return url;
        }
        return url.substring(0, index);
    }

    /**
     * 初始化文件上传事件
     * 缓存相关的信息
     * @param filename
     * @param cacheKey
     * @return
     */
    private ChunkUploadEntity initChunkUploadInfo(String filename, String cacheKey) {
        String realPath = getFilePath(FileUtil.getFileSuffix(filename));

        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(config.getBucketName(), realPath);
        InitiateMultipartUploadResult result = ossClient.initiateMultipartUpload(request);
        if (result == null) {
            throw new MPanFrameworkException("初始化文件上传事件失败");
        }
        ChunkUploadEntity entity = new ChunkUploadEntity();
        entity.setRealPath(realPath);
        entity.setUploadId(result.getUploadId());
        getCache().put(cacheKey, entity);
        return entity;
    }

    private String getCacheKey(String identifier, Long userId) {
        return String.format(CACHE_KEY_TEMPLATE, identifier, userId);
    }


    /**
     * 提供的功能
     * 根据提供的分片文件的信息，执行分片文件的合并操作
     * OOS分片合并的业务逻辑
     * 1.根据分片信息提取出PartETag信息
     * 2.根据PartETag列表执行分片合并操作
     * @param context
     * @throws IOException
     */
    @Override
    protected void doMergeChunk(MergeChunkContext context) throws IOException {
        String cacheKey = getCacheKey(context.getIdentifier(),context.getUserId());
        ChunkUploadEntity entity = getCache().get(cacheKey, ChunkUploadEntity.class);
        if (entity == null) {
            throw new MPanFrameworkException("分片上传信息不存在");
        }
        List<String> chunkPathList = context.getChunkPathList();
        List<PartETag> partETagList = chunkPathList.stream().map(path -> {
            Map<String,String> paramMap = getParamMap(path);
            if (paramMap == null) {
                return null;
            }
            return new PartETag(Integer.parseInt(paramMap.get(PART_NUMBER_KEY)),paramMap.get(E_TAG_KEY));
        }
        ).filter(Objects::nonNull).collect(Collectors.toList());
        CompleteMultipartUploadRequest request = new CompleteMultipartUploadRequest(config.getBucketName(), entity.getRealPath(), entity.getUploadId(), partETagList);
        CompleteMultipartUploadResult result = ossClient.completeMultipartUpload(request);
        if (result == null) {
            throw new MPanFrameworkException("分片合并失败");
        }
        getCache().evict(cacheKey);
        context.setRealFilePath(entity.getRealPath());
    }

    private Map<String,String> getParamMap(String path) {
        int index = path.indexOf("?");
        if (index == -1) {
            return null;
        }
        String params = path.substring(index + 1);
        HashMap<String,String> map = new HashMap<>();
        Arrays.stream(params.split("&")).forEach(param -> {
            String[] kv = param.split("=");
            map.put(kv[0],kv[1]);
        });
        return map;
    }

    @Override
    protected void doReadFile(ReadFileContext context) throws IOException {
        OSSObject ossObject = ossClient.getObject(config.getBucketName(), context.getRealFilePath());
        if (ossObject == null) {
            throw new MPanFrameworkException("文件不存在");
        }
        FileUtil.writeStream2Stream(ossObject.getObjectContent(),context.getOutputStream());
    }

    /*----------------------------------------------------------------------------private--------------------------------------------------------------------------------------------------*/

    /**
     * 获取 OSS对象完整名称
     * 格式 /年/月/日/UUID.文件后缀
     *
     * @param fileSuffix
     * @return 年/月/日/UUID.文件后缀
     */
    private String getFilePath(String fileSuffix) {
        return new StringBuffer().append(DateUtil.thisYear()).append(MPanConstants.SLASH_STR).append(DateUtil.thisMonth() + 1).append(MPanConstants.SLASH_STR).append(DateUtil.thisDayOfMonth()).append(MPanConstants.SLASH_STR).append(UUIDUtil.getUUID()).append(fileSuffix).toString();
    }

    /**
     * 文件分片上传初始化之后的全局唯一信息实体
     */
    @Data
    public static class ChunkUploadEntity implements Serializable {
        /**
         * 上传ID
         */
        private String uploadId;

        /**
         * 对应的就是对象的完整名称
         */
        private String realPath;
    }
}
