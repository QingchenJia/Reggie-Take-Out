package reggietakeout.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
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
}
