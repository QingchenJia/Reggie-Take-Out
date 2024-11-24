package reggietakeout.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {
    /**
     * 配置RedisTemplate以支持Spring应用中的Redis操作
     * 此方法主要负责初始化RedisTemplate，并设置其连接工厂和序列化方式
     *
     * @param redisCommandFactory Redis连接工厂，用于创建与Redis服务器的连接
     * @return 配置完成的RedisTemplate实例，可用于执行Redis操作
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisCommandFactory) {
        // 创建RedisTemplate实例
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // 设置Redis连接工厂
        redisTemplate.setConnectionFactory(redisCommandFactory);

        // 设置键的序列化方式为字符串序列化
        redisTemplate.setKeySerializer(RedisSerializer.string());
        // 设置哈希键的序列化方式为字符串序列化
        redisTemplate.setHashKeySerializer(RedisSerializer.string());

        // 返回配置完成的RedisTemplate实例
        return redisTemplate;
    }
}
