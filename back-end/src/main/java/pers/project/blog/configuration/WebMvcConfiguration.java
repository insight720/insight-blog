package pers.project.blog.configuration;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring.http.converter.FastJsonHttpMessageConverter;
import com.alibaba.fastjson2.support.spring.webservlet.view.FastJsonJsonView;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pers.project.blog.handler.PaginationHandlerInterceptor;

import java.util.Collections;
import java.util.List;

/**
 * Spring MVC 配置类
 *
 * @author Luo Fei
 * @date 2022/12/27
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final PaginationHandlerInterceptor paginationHandlerInterceptor;

    public WebMvcConfiguration(PaginationHandlerInterceptor paginationHandlerInterceptor) {
        this.paginationHandlerInterceptor = paginationHandlerInterceptor;
    }

    /**
     * 序列化配置
     *
     * @return <a href="https://github.com/alibaba/fastjson2/blob/main/docs/spring_support_cn.md">FastJson2 配置</a>
     */
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
        converters.add(0, converter);
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        FastJsonJsonView fastJsonJsonView = new FastJsonJsonView();
        fastJsonJsonView.setFastJsonConfig(fastJsonConfig());
        registry.enableContentNegotiation(fastJsonJsonView);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(paginationHandlerInterceptor);
    }

    // TODO: 2022/12/28 未开启跨域配置
/*
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedHeaders("*")
                .allowedMethods("*")
                .exposedHeaders("*")
                .allowedOriginPatterns("*");
    }
*/

}
