package reggietakeout.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import reggietakeout.common.JacksonObjectMapper;
import reggietakeout.interceptor.LoginCheckInterceptor;

import java.util.List;

@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Autowired
    private LoginCheckInterceptor loginCheckInterceptor;

    /**
     * 重新映射静态资源路径
     *
     * @param registry 资源处理器注册表，用于注册资源处理器
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 "/backend/**" 路径下的请求映射到类路径下的 "/backend/" 目录
        registry.addResourceHandler("/backend/**")
                .addResourceLocations("classpath:/backend/");

        // 将 "/front/**" 路径下的请求映射到类路径下的 "/front/" 目录
        registry.addResourceHandler("/front/**")
                .addResourceLocations("classpath:/front/");
    }

    /**
     * 扩展消息转换器列表
     * <p>
     * 该方法用于向Spring的HTTP消息转换器列表中添加自定义的消息转换器
     * 通过覆盖此方法，可以定义如何处理HTTP请求和响应的数据序列化和反序列化
     *
     * @param converters 消息转换器列表，可以添加、修改或移除其中的转换器
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 创建一个MappingJackson2HttpMessageConverter实例，用于处理JSON数据的转换
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();

        // 设置自定义的ObjectMapper，以实现特定的序列化和反序列化逻辑
        messageConverter.setObjectMapper(new JacksonObjectMapper());

        // 将自定义的消息转换器添加到列表的最前面，确保它优先被使用
        converters.addFirst(messageConverter);
    }

    /**
     * 添加拦截器配置
     * <p>
     * 该方法用于向应用程序添加拦截器，以在请求处理之前或之后执行特定逻辑
     * 在本例中，我们添加了一个登录检查拦截器，以确保只有经过身份验证的用户才能访问受保护的资源
     *
     * @param registry 拦截器注册表，用于添加和配置拦截器
     */
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        // 注册登录检查拦截器，并应用到几乎所有路径
        // 但排除了一些特定路径，如登录和注销路径，以及前端和后端的静态资源路径
        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/employee/login",
                        "/employee/logout",
                        "/backend/**",
                        "/front/**",
                        "/user/sendMsg",
                        "/user/login",
                        "/user/logout"
                );
    }
}
