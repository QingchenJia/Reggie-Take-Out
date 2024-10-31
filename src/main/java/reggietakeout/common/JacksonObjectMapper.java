package reggietakeout.common;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * 自定义Jackson ObjectMapper类
 * 用于配置全局的序列化和反序列化规则
 */
public class JacksonObjectMapper extends ObjectMapper {
    // 定义默认的日期格式
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    // 定义默认的日期时间格式
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    // 定义默认的时间格式
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    /**
     * 构造函数
     * 初始化ObjectMapper，并配置通用的序列化和反序列化规则
     */
    public JacksonObjectMapper() {
        super();
        // 收到未知属性时不报异常
        this.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 反序列化时，属性不存在的兼容处理
        this.getDeserializationConfig().withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // 创建一个简单的功能模块，用于注册自定义的序列化器和反序列化器
        SimpleModule simpleModule = new SimpleModule()
                // 注册LocalDateTime的反序列化器
                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)))
                // 注册LocalDate的反序列化器
                .addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)))
                // 注册LocalTime的反序列化器
                .addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)))

                // 注册BigInteger的序列化器，将其序列化为字符串
                .addSerializer(BigInteger.class, ToStringSerializer.instance)
                // 注册Long的序列化器，将其序列化为字符串
                .addSerializer(Long.class, ToStringSerializer.instance)
                // 注册LocalDateTime的序列化器
                .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)))
                // 注册LocalDate的序列化器
                .addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)))
                // 注册LocalTime的序列化器
                .addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));

        // 注册功能模块，例如，可以添加自定义序列化器和反序列化器
        this.registerModule(simpleModule);
    }
}
