package reggietakeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import reggietakeout.entity.Employee;
import reggietakeout.exception.UsernameRepeatException;
import reggietakeout.mapper.EmployeeMapper;
import reggietakeout.service.EmployeeService;

import java.time.LocalDateTime;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
    /**
     * 根据用户名选择员工信息
     *
     * @param username 用户名，用于查询员工的唯一标识
     * @return 返回查询到的员工对象，如果没有找到则返回null
     */
    @Override
    public Employee selectByUsername(String username) {
        // 创建LambdaQueryWrapper对象，用于封装查询条件
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        // 设置查询条件，查找用户名与参数username相等的员工
        queryWrapper.eq(Employee::getUsername, username);
        // 使用封装的查询条件，从数据库中获取唯一的员工对象
        return getOne(queryWrapper);
    }

    /**
     * 插入员工信息
     * <p>
     * 此方法用于向系统中添加新的员工记录在执行插入操作前，它会设置员工的状态、密码、
     * 创建和更新时间以及创建和更新用户ID这些默认值是为了确保新员工账户的一致性和安全性
     *
     * @param employee 待插入的员工对象，包含员工的相关信息
     * @param userId   创建用户的ID，用于记录是谁添加了该员工
     */
    @Override
    public void insert(Employee employee, Long userId) {
        // 获取员工用户名，用于检查是否重复
        String username = employee.getUsername();

        // 检查用户名是否已存在，如果存在则抛出异常
        if (selectByUsername(username) != null)
            throw new UsernameRepeatException("用户名已存在");

        // 设置员工初始状态为0，表示该员工账户是激活的
        employee.setStatus(0);
        // 使用MD5加密算法对默认密码"123456"进行加密处理，确保密码安全性
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        // 设置创建时间和更新时间为当前系统时间，表示员工信息的创建和最后修改时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        // 设置创建用户ID和更新用户ID为传入的userId，记录是谁创建和更新了该员工信息
        employee.setCreateUser(userId);
        employee.setUpdateUser(userId);

        // 调用save方法保存员工信息到数据库
        save(employee);
    }
}
