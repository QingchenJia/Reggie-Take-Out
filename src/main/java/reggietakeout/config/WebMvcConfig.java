package reggietakeout.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import reggietakeout.common.JacksonObjectMapper;

import java.util.List;

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
}
