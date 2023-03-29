package pers.project.blog.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static pers.project.blog.constant.FilePathConst.DOT;

/**
 * 文件 IO 工具类
 *
 * @author Luo Fei
 * @version 2023/1/3
 */
public abstract class FileIoUtils {

    /**
     * 返回用文件十六进制 MD5 摘要表示的新文件名
     * <p>
     * <b>注意：新文件名包含扩展名，且此方法不会关闭文件输入流</b>
     *
     * @param multipartFile 文件
     * @return 新文件名
     */
    @NotNull
    public static String getNewFileName(@NotNull MultipartFile multipartFile) {
        String md5DigestAsHex;
        try {
            // 这里已经计算过内容的 MD5 值，可以在上传时进行 Base64 编码并加入到上传请求中，但改动太大
            md5DigestAsHex = DigestUtils.md5DigestAsHex(multipartFile.getInputStream());
        } catch (IOException cause) {
            throw new RuntimeException("获取新文件名异常", cause);
        }
        String extensionName = getExtensionName(multipartFile.getOriginalFilename());
        return md5DigestAsHex + DOT + extensionName;
    }

    /**
     * 获得文件的扩展名（后缀名），扩展名不带 "."
     *
     * @param fileName 文件名
     * @return 扩展名
     */
    public static String getExtensionName(String fileName) {
        return FileNameUtil.getSuffix(fileName);
    }

    /**
     * 获取主文件名
     *
     * @param fileName 完整文件名
     * @return 主文件名
     */
    public static String getMainName(String fileName) {
        return FileNameUtil.getPrefix(fileName);
    }

    /**
     * 拷贝字符流，拷贝后不会关闭流
     *
     * @param reader 输入流
     * @param writer 输出流
     */
    public static void copyCharStream(Reader reader, Writer writer) {
        IoUtil.copy(reader, writer);
    }

    /**
     * 拷贝字节流，拷贝后不会关闭流
     *
     * @param in  输入流
     * @param out 输出流
     */
    public static void copyByteStream(InputStream in, OutputStream out) {
        IoUtil.copy(in, out);
    }

    /**
     * 从流中读取字节数组，读取后关闭流
     *
     * @param in 输入流
     * @return 字节数组
     */
    public static byte[] readBytes(InputStream in) {
        return IoUtil.readBytes(in);
    }

    /**
     * 从流中按行读取字符，读取后关闭流
     *
     * @param in 输入流
     * @return 行数据列表
     */
    public static List<String> readLines(InputStream in) {
        return IoUtil.readLines(in, StandardCharsets.UTF_8, new ArrayList<>());
    }

    /**
     * 关闭可关闭对象
     *
     * @param autoCloseable 被关闭的对象
     */
    public static void close(AutoCloseable autoCloseable) {
        if (autoCloseable != null) {
            try {
                autoCloseable.close();
            } catch (Exception cause) {
                throw new RuntimeException("关闭对象异常", cause);
            }
        }
    }

}
