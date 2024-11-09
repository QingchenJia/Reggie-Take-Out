package reggietakeout.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 在插入记录时填充创建时间和创建用户
     *
     * @param metaObject 元数据对象，用于操作实体类的属性
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 插入时填充 createTime 和 createUser
        setFieldIfPresent(metaObject, "createTime", LocalDateTime.now());
        setFieldIfPresent(metaObject, "createUser", BaseContext.getCurrentId());

        // 同时填充 updateTime 和 updateUser，因为在插入时也是第一次更新
        setFieldIfPresent(metaObject, "updateTime", LocalDateTime.now());
        setFieldIfPresent(metaObject, "updateUser", BaseContext.getCurrentId());
    }

    /**
     * 更新记录时填充更新时间和更新用户
     * <p>
     * 在更新数据库记录时，有两个字段需要自动填充：
     * - updateTime：记录最后一次更新的时间
     * - updateUser：记录最后一次更新的用户
     *
     * @param metaObject 元数据对象，用于反射设置字段值
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时填充 updateTime 和 updateUser
        setFieldIfPresent(metaObject, "updateTime", LocalDateTime.now());
        setFieldIfPresent(metaObject, "updateUser", BaseContext.getCurrentId());
    }

    /**
     * 如果字段存在，则设置该字段的值
     * 此方法用于在反射操作中，仅当字段存在时才更新字段值，避免引发字段不存在的异常
     *
     * @param metaObject MetaObject封装了对对象的反射操作，提供统一的属性访问接口
     * @param fieldName  需要设置的字段名称
     * @param value      要为字段设置的值
     */
    private void setFieldIfPresent(MetaObject metaObject, String fieldName, Object value) {
        // 检查字段是否存在并进行填充
        if (metaObject.hasSetter(fieldName)) {
            metaObject.setValue(fieldName, value);
        }
    }
}

