package pers.project.blog.util;


import cn.hutool.extra.servlet.ServletUtil;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.dreamlu.mica.ip2region.core.Ip2regionSearcher;
import net.dreamlu.mica.ip2region.core.IpInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;


/**
 * Web 工具类
 *
 * @author Luo Fei
 * @date 2022/12/26
 */
@Component
@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebUtils {

    /**
     * IP 信息搜索器
     */
    private static Ip2regionSearcher regionSearcher;

    /**
     * 用于执行 HTTP 请求
     */
    private static RestTemplate restTemplate;

    @Autowired
    public void setRegionSearcher(Ip2regionSearcher regionSearcher) {
        WebUtils.regionSearcher = regionSearcher;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        WebUtils.restTemplate = restTemplate;
    }

    /**
     * 执行 GET 请求。响应（如果有）将被转换并返回。
     *
     * @param url          URL
     * @param responseType 返回值的类型
     * @param uriVariables 请求参数的映射
     * @return 转换后的对象
     */
    @Nullable
    public static <T> T get(@NotNull String url, @NotNull Class<T> responseType,
                            @NotNull Map<String, ?> uriVariables) {
        return restTemplate.getForObject(url, responseType, uriVariables);
    }

    /**
     * 执行 HTTP 方法，将给定的请求实体写入请求，并将响应返回为 ResponseEntity。
     *
     * @param url           URL
     * @param method        HTTP 请求方法
     * @param requestEntity 要写入请求的实体（标头 和/或 正文），可能是 null
     * @param responseType  要将响应转换为的类型，或者用 Void.class 没有正文的类型
     * @param uriVariables  要在模板中扩展的变量
     * @return 作为实体的响应
     */
    @NotNull
    public static <T> ResponseEntity<T> exchange(@NotNull String url, @NotNull HttpMethod method,
                                                 @Nullable HttpEntity<?> requestEntity,
                                                 @NotNull Class<T> responseType,
                                                 @NotNull Map<String, ?> uriVariables) {
        return restTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
    }

    /**
     * 响应 JSON 字符串
     *
     * @param response HTTP 响应
     * @param result   结果对象
     */
    public static void renderJson(@NotNull HttpServletResponse response,
                                  @NotNull Object result) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String content = ConvertUtils.getJson(result);
        response.getWriter().write(content);
    }

    /**
     * 获取当前的 HTTP 请求
     *
     * @return HTTP 请求
     */
    public static HttpServletRequest getCurrentRequest() {
        return (HttpServletRequest) RequestContextHolder
                .currentRequestAttributes()
                .resolveReference(RequestAttributes.REFERENCE_REQUEST);
    }

    /**
     * 获取当前的 HTTP 请求
     *
     * @return HTTP 会话
     */
    public static HttpSession getCurrentSession() {
        return (HttpSession) RequestContextHolder
                .currentRequestAttributes()
                .resolveReference(RequestAttributes.REFERENCE_SESSION);
    }

    /**
     * 获取 IP 地址
     *
     * @param request HTTP 请求
     * @return IP 地址
     */
    public static String getIpAddress(@NotNull HttpServletRequest request) {
        return ServletUtil.getClientIP(request);
    }

    /**
     * 获取当前 HTTP 请求的 IP 地址
     *
     * @return IP 地址
     */
    public static String getCurrentIpAddress() {
        return ServletUtil.getClientIP(getCurrentRequest());
    }

    /**
     * 获取 IP 来源
     *
     * @param ipAddress IP 地址
     * @return IP 来源
     */
    @Nullable
    public static String getIpSource(@NotNull String ipAddress) {
        return regionSearcher.getAddress(ipAddress);
    }

    /**
     * 获取当前 HTTP 请求的 IP 来源
     *
     * @return IP 来源
     */
    @Nullable
    public static String getCurrentIpSource() {
        return regionSearcher.getAddress(getCurrentIpAddress());
    }

    /**
     * 获取指定的 IP 信息
     *
     * @param ipAddress IP 地址
     * @param function  方法引用
     * @return {@link IpInfo} 的信息
     */
    @Nullable
    public static String getInfo(@NotNull String ipAddress,
                                 @NotNull Function<IpInfo, String> function) {
        return regionSearcher.getInfo(ipAddress, function);
    }

    /**
     * 获取当前当前 HTTP 请求的指定 IP 信息
     *
     * @param function 方法引用
     * @return {@link IpInfo} 的信息
     */
    @Nullable
    public static String getCurrentInfo(@NotNull Function<IpInfo, String> function) {
        return regionSearcher.getInfo(getCurrentIpAddress(), function);
    }

    /**
     * 获取用户代理信息
     *
     * @param request 请求
     * @return 用户代理信息的容器类
     */
    @NotNull
    public static UserAgent getUserAgent(@NotNull HttpServletRequest request) {
        return UserAgent.parseUserAgentString
                (request.getHeader(HttpHeaders.USER_AGENT));
    }

    /**
     * 获取当前当前 HTTP 请求的用户代理信息
     *
     * @return 用户代理信息的容器类
     */
    @NotNull
    public static UserAgent getCurrentUserAgent() {
        return UserAgent.parseUserAgentString
                (getCurrentRequest().getHeader(HttpHeaders.USER_AGENT));
    }

}

