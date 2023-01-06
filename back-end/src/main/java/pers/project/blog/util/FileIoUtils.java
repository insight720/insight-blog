package pers.project.blog.util;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.DigestUtils;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.constant.FileIoConstant;

import java.io.*;


/**
 * 文件 IO 工具类
 *
 * @author Luo Fei
 * @date 2023/1/3
 */
@Slf4j
public abstract class FileIoUtils {

    /**
     * 返回用文件十六进制 MD5 摘要表示的新文件名，包含扩展名
     * <p>
     * <b>注意：新文件名包含扩展名，且此方法不会关闭文件输入流</b>
     *
     * @param multipartFile 多部分请求中收到的上传文件
     * @return 新文件名
     * @throws IOException IO 异常
     */
    public static String getNewFileName(MultipartFile multipartFile) throws IOException {
        String md5DigestAsHex = DigestUtils.md5DigestAsHex(multipartFile.getInputStream());
        String extensionName = getExtensionName(multipartFile.getOriginalFilename());
        return md5DigestAsHex + FileIoConstant.DOT + extensionName;
    }

    /**
     * 获得文件的扩展名（后缀名），扩展名不带 "."
     *
     * @param fileName 文件名
     * @return 扩展名
     */
    @Nullable
    public static String getExtensionName(String fileName) {
        if (fileName == null) {
            return null;
        }
        int index = fileName.lastIndexOf(FileIoConstant.DOT);
        if (index == -1) {
            return null;
        } else {
            String extensionName = fileName.substring(index + 1);
            // 扩展名中不能包含路径相关的符号
            return extensionName.contains(FileIoConstant.SEPARATOR)
                    || extensionName.contains(FileIoConstant.WINDOWS_SEPARATOR) ?
                    null : extensionName;
        }
    }

    /**
     * 拷贝字符流，拷贝后不会关闭流
     *
     * @param reader 输入流
     * @param writer 输出流
     * @throws IOException IO 异常
     */
    public static void copyCharStream(Reader reader, Writer writer) throws IOException {
        char[] buffer = new char[FileIoConstant.DEFAULT_BUFFER_SIZE];
        int readLength;
        while ((readLength = reader.read(buffer)) != FileIoConstant.EOF) {
            writer.write(buffer, 0, readLength);
        }
        // 确保写入流的字符传递给操作系统进行写入
        writer.flush();
    }

    /**
     * 拷贝字节流，使用默认 Buffer 大小，拷贝后不会关闭流
     *
     * @param in  输入流
     * @param out 输出流
     * @throws IOException IO 异常
     */
    public static void copyByteStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[FileIoConstant.DEFAULT_BUFFER_SIZE];
        int inputLength;
        while ((inputLength = in.read(buffer)) != FileIoConstant.EOF) {
            out.write(buffer, 0, inputLength);
        }
    }

    /**
     * 从流中缓冲读取字节数组，拷贝后关闭流
     *
     * @param in {@link InputStream}
     * @return 字节数组
     * @throws IOException IO异常
     */
    public static byte[] readBytes(InputStream in) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(in);
             FastByteArrayOutputStream out = new FastByteArrayOutputStream()) {
            copyByteStream(bis, out);
            return out.toByteArrayUnsafe();
        }
    }

    // TODO: 2023/1/4 可以使用全局异常处理

    /**
     * 关闭
     * <p>
     * <b>注意：关闭失败不会抛异常</b>
     *
     * @param autoCloseable 被关闭的对象
     */
    public static void close(AutoCloseable autoCloseable) {
        if (autoCloseable != null) {
            try {
                autoCloseable.close();
            } catch (Exception e) {
                log.error("关闭异常", e);
            }
        }
    }

}
