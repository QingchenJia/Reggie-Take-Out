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
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        // 同时填充 updateTime 和 updateUser，因为在插入时也是第一次更新
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
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
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
}

