package reggietakeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import reggietakeout.entity.User;
import reggietakeout.mapper.UserMapper;
import reggietakeout.service.UserService;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    /**
     * 插入新用户或返回已存在的用户
     *
     * @param phone 用户的手机号码，用于查询和创建用户
     * @return 如果数据库中不存在该用户，则创建并返回新用户；否则返回已存在的用户
     */
    @Override
    public User insertUser(String phone) {
        // 查询数据库中是否存在相同手机号码的用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);

        User userdb = getOne(queryWrapper);

        // 如果数据库中不存在该用户，则创建并保存新用户
        if (userdb == null) {
            User user = new User();
            user.setPhone(phone);
            user.setStatus(1);
            save(user);

            return user;
        }

        // 如果数据库中已存在该用户，则直接返回
        return userdb;
    }
}
