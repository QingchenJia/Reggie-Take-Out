package reggietakeout.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import reggietakeout.interceptor.LoginCheckInterceptor;

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

        // 映射Swagger UI的文档请求
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        // 映射webjars资源请求
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        // 映射favicon.ico请求
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/META-INF/resources/");
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
                        // 静态资源路径
                        "/backend/**",
                        "/front/**",
                        // 登录和注销路径
                        "/employee/login",
                        "/employee/logout",
                        "/user/sendMsg",
                        "/user/login",
                        "/user/loginout",
                        // Swagger UI相关路径
                        "/doc.html",
                        "/webjars/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/favicon.ico"
                );
    }
}
