package pers.project.blog.config;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring.http.converter.FastJsonHttpMessageConverter;
import com.alibaba.fastjson2.support.spring.webservlet.view.FastJsonJsonView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pers.project.blog.handler.AccessLimitHandlerInterceptor;
import pers.project.blog.handler.PageHandlerInterceptor;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

import static pers.project.blog.constant.GenericConst.ZERO;

/**
 * Spring MVC 配置类
 *
 * @author Luo Fei
 * @version 2022/12/27
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private AccessLimitHandlerInterceptor accessLimitHandlerInterceptor;

    @Resource
    private PageHandlerInterceptor pageHandlerInterceptor;

    /**
     * 执行 HTTP 请求
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // region FastJson2

    /**
     * 序列化配置
     * <p>
     * 参见 <a href="https://github.com/alibaba/fastjson2/blob/main/docs/spring_support_cn.md">FastJson2 配置</a>
     */
    @Bean
    public FastJsonConfig fastJsonConfig() {
        FastJsonConfig config = new FastJsonConfig();
        config.setReaderFeatures(JSONReader.Feature.FieldBased);
        config.setWriterFeatures(JSONWriter.Feature.FieldBased);
        return config;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        converter.setFastJsonConfig(fastJsonConfig());
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
        converters.add(ZERO, converter);
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        FastJsonJsonView fastJsonJsonView = new FastJsonJsonView();
        fastJsonJsonView.setFastJsonConfig(fastJsonConfig());
        registry.enableContentNegotiation(fastJsonJsonView);
    }

    // endregion

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessLimitHandlerInterceptor);
        registry.addInterceptor(pageHandlerInterceptor);
    }

}
