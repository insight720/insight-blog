package pers.project.blog.util;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.regex.Pattern;

import static pers.project.blog.constant.GenericConst.EMPTY_STR;

/**
 * 字符串和正则表达式工具类
 *
 * @author Luo Fei
 * @version 2022/12/23
 */
public abstract class StrRegexUtils {

    /**
     * {@link StrRegexUtils#filter(String)}
     * 方法的正则表达式（保留图片标签）
     */
    public static final String[] FILTER_REGEXPS = new String[]
            {"(?!<(img).*?>)<.*?>", "(onload(.*?)=)", "(onerror(.*?)=)"};
    /**
     * {@link StrRegexUtils#deleteTag(String)} 方法的正则表示式
     */
    public static final String[] DELETE_TAG_REGEXPS = new String[]
            {       // 转义字符
                    "&.{2,6}?;",
                    // script 标签
                    "<\\s*?script[^>]*?>[\\s\\S]*?<\\s*?/\\s*?script\\s*?>",
                    // style 标签
                    "<\\s*?style[^>]*?>[\\s\\S]*?<\\s*?/\\s*?style\\s*?>"
            };
    /**
     * Email 正则表达式，来自 <a href="http://emailregex.com/">Email Regex</a>
     */
    private static final String EMAIL_REGEX
            = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01" +
            "-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0" +
            "c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]" +
            "*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4" +
            "][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\" +
            "x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])";
    /**
     * Email 正则表达式的编译表示形式
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    /**
     * 敏感词引导类
     */
    private static final SensitiveWordBs WORD_BS = SensitiveWordBs.newInstance()
            // 在这里添加允许的敏感词
            // COS URL 里含有 64
            .wordAllow(() -> Collections.singletonList("64"))
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
     * 检查字符序列是否是 null、空（""）、或者空格
     * <p>
     * 空格由 {@link Character#isWhitespace(char)} 定义。
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param charSequence – 要检查的字符序列，可能为空
     * @return true 如果字符序列是 null、空（""）、或者空格
     */
    public static boolean isBlank(@Nullable CharSequence charSequence) {
        if (charSequence == null) {
            return true;
        }
        int length = charSequence.length();
        if (length == 0) {
            return true;
        }
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(charSequence.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查字符序列是否不为 null、空（""）、或者空格
     * <p>
     * 空格由 {@link Character#isWhitespace(char)} 定义。
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param charSequence – 要检查的字符序列，可能为空
     * @return true 如果字符序列不为 null、空（""）、或者空格
     */
    public static boolean isNotBlank(CharSequence charSequence) {
        return !isBlank(charSequence);
    }

    /**
     * 过滤文本
     *
     * @param source 的文本
     * @return 过滤后的文本
     */
    @NotNull
    public static String filter(@NotNull String source) {
        // 敏感词过滤
        source = WORD_BS.replace(source);
        // 过滤标签（保留图片标签）
        for (String regex : FILTER_REGEXPS) {
            source = source.replaceAll(regex, EMPTY_STR);
        }
        return deleteTag(source);
    }

    /**
     * 删除标签
     *
     * @param source 文本
     * @return 删除后的文本
     */
    @NotNull
    public static String deleteTag(@NotNull String source) {
        for (String regexp : DELETE_TAG_REGEXPS) {
            source = source.replaceAll(regexp, EMPTY_STR);
        }
        return source;
    }

    /**
     * 验证电子邮箱
     *
     * @param email 电子邮件地址
     * @return 是否是电子邮箱
     */
    public static boolean checkEmail(@NotNull String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 获取括号内内容
     *
     * @param str str
     * @return 括号内容
     */
    @NotNull
    public static String getBracketsContent(@NotNull String str) {
        return str.substring(str.indexOf("(") + 1, str.indexOf(")"));
    }

}
