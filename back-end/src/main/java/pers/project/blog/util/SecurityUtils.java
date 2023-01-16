package pers.project.blog.util;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import org.springframework.security.core.context.SecurityContextHolder;
import pers.project.blog.dto.UserDetailsDTO;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

/**
 * 安全相关的工具类
 *
 * @author Luo Fei
 * @date 2022/12/23
 */
public abstract class SecurityUtils {

    /**
     * 电子邮件正则表达式，来自 <a href="http://emailregex.com/">emailregex.com</a>
     */
    private static final String EMAIL_REGEX
            = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01" +
            "-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0" +
            "c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]" +
            "*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4" +
            "][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\" +
            "x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])";
    public static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private static final SensitiveWordBs WORD_BS = SensitiveWordBs.newInstance()
            .ignoreCase(true)
            .ignoreWidth(true)
            .ignoreNumStyle(true)
            .ignoreChineseStyle(true)
            .ignoreEnglishStyle(true)
            .ignoreRepeat(true)
            .enableNumCheck(false)
            .enableEmailCheck(false)
            .enableUrlCheck(false)
            .init();

    /**
     * 获取当前登录用户的信息
     *
     * @return {@link UserDetailsDTO} 提供核心用户信息
     */
    public static UserDetailsDTO getUserDetails() {
        // TODO: 2022/12/28 测试使用
        UserDetailsDTO principal = new UserDetailsDTO();
        principal.setUserInfoId(1);
        try {
            principal = (UserDetailsDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return principal;
    }

    /**
     * 删除标签
     *
     * @param source 需要进行剔除HTML的文本
     * @return 过滤后的内容
     */
    public static String filter(String source) {
        // 敏感词过滤
        source = WORD_BS.replace(source);
        // 保留图片标签
        source = source.replaceAll("(?!<(img).*?>)<.*?>", "")
                .replaceAll("(onload(.*?)=)", "")
                .replaceAll("(onerror(.*?)=)", "");
        return deleteHMTLTag(source);
    }

    /**
     * 删除标签
     *
     * @param source 文本
     * @return 过滤后的文本
     */
    public static String deleteHMTLTag(String source) {
        // 删除转义字符
        source = source.replaceAll("&.{2,6}?;", "");
        // 删除script标签
        source = source.replaceAll("<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>", "");
        // 删除style标签
        source = source.replaceAll("<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>", "");
        return source;
    }

    // TODO: 2023/1/14 邮件 ？ username？

    /**
     * 验证电子邮箱
     *
     * @param username 电子邮件地址
     * @return 是否是电子邮箱
     */
    public static boolean checkEmail(String username) {
        return EMAIL_PATTERN.matcher(username).matches();
    }

    /**
     * 获取 6 位随机数字验证码
     *
     * @return 6 位随机数字自负床
     */
    public static String getRandomCode() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            stringBuilder.append(random.nextInt(10));
        }
        return stringBuilder.toString();
    }

    /**
     * 获取括号内容
     *
     * @param str str
     * @return {@link String} 括号内容
     */
    public static String getBracketsContent(String str) {
        return str.substring(str.indexOf("(") + 1, str.indexOf(")"));
    }

}
