package com.pandaer.pan.core.utils;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.pandaer.pan.core.constants.MPanConstants;
import com.pandaer.pan.core.exception.MPanBusinessException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;

public class FileUtil {

    public static String getFileSuffix(String filename) {
        if (StringUtils.isBlank(filename) || filename.lastIndexOf(MPanConstants.POINT_STR) == -1) {
            return MPanConstants.EMPTY_STR;
        }
        int lastIndex = filename.lastIndexOf(MPanConstants.POINT_STR);
        return filename.substring(lastIndex);
    }

    public static String byteCount2DisplaySize(Long totalSize) {
        if (Objects.isNull(totalSize)) {
            return MPanConstants.EMPTY_STR;
        }
        return FileUtils.byteCountToDisplaySize(totalSize);
    }

    public static String getFilePreviewContentType(String fileSuffix) {
        //todo 根据文件后缀返回 HTTP中的Content-Type中的值
        return "";
    }

    public static void deleteFile(List<String> realFilePathList) throws IOException {
        if (CollUtil.isEmpty(realFilePathList)) {
            throw new MPanBusinessException("物理路径列表为空");
        }
        for (String realPath : realFilePathList) {
            FileUtils.forceDelete(new File(realPath));
        }

    }

    /**
     * 生成文件的存储路径
     * 基础路径 + 年 + 月 + 日
     * @param basePath
     * @param filename
     * @return
     */
    public static String genRealFilePath(String basePath, String filename) {
        return new StringBuffer(basePath)
                .append(File.separator)
                .append(DateUtil.thisYear())
                .append(File.separator)
                .append(DateUtil.thisMonth() + 1)
                .append(File.separator)
                .append(DateUtil.thisDayOfMonth())
                .append(File.separator)
                .append(UUIDUtil.getUUID())
                .append(getFileSuffix(filename)).toString();
    }

    /**
     * 使用底层 零拷贝技术
     * 1.创建一个新的文件作为数据的载体
     * 2.利用底层的sendFile将数据写入file中
     * @param inputStream
     * @param file
     * @param totalSize
     */
    public static void writeStream2File(InputStream inputStream, File file, Long totalSize) throws IOException {
        createRealFile(file);
        RandomAccessFile randomAccessFile = new RandomAccessFile(file,"rw");
        FileChannel output = randomAccessFile.getChannel();
        ReadableByteChannel input = Channels.newChannel(inputStream);
        output.transferFrom(input,0L,totalSize);
        //关闭相关资源
        randomAccessFile.close();
        output.close();
        input.close();
    }

    public static void createRealFile(File file) throws IOException {
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        file.createNewFile();
    }

    public static String genDefaultBasePath() {
        return new StringBuffer(System.getProperty("user.home"))
                .append(File.separator)
                .append("pan").toString();
    }

    public static String genDefaultChunkBasePath() {
        return new StringBuffer(System.getProperty("user.home"))
                .append(File.separator)
                .append("pan")
                .append(File.separator)
                .append("chunks")
                .toString();
    }

    public static String genRealChunkFilePath(String basePath, String identifier, Integer currentChunkNumber) {
        return new StringBuffer(basePath)
                .append(File.separator)
                .append(DateUtil.thisYear())
                .append(File.separator)
                .append(DateUtil.thisMonth() + 1)
                .append(File.separator)
                .append(DateUtil.thisDayOfMonth())
                .append(File.separator)
                .append(identifier)
                .append(File.separator)
                .append(UUIDUtil.getUUID())
                .append("_")
                .append(currentChunkNumber).toString();
    }

    public static void appendWrite(Path target, Path source) throws IOException {
        Files.write(target,Files.readAllBytes(source), StandardOpenOption.APPEND);
    }

    public static void writeFile2OutputStream(FileInputStream fileInputStream, OutputStream outputStream, long length) {
        try  {
            FileChannel fileChannel = fileInputStream.getChannel();
            fileChannel.transferTo(0,length,Channels.newChannel(outputStream));
            outputStream.flush();
            outputStream.close();
            fileInputStream.close();
            fileChannel.close();
        } catch (IOException e) {
            throw new MPanBusinessException("文件写入输出流失败");
        }
    }
}
