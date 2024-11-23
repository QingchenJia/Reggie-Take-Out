package reggietakeout.service.impl;

import ch.qos.logback.core.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import reggietakeout.entity.Employee;
import reggietakeout.exception.CustomException;
import reggietakeout.mapper.EmployeeMapper;
import reggietakeout.service.EmployeeService;

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
     */
    @Override
    public void insert(Employee employee) {
        // 获取员工用户名，用于检查是否重复
        String username = employee.getUsername();

        // 检查用户名是否已存在，如果存在则抛出异常
        if (selectByUsername(username) != null)
            throw new CustomException("用户名已存在");

        // 设置员工初始状态为1，表示该员工账户是激活的
        employee.setStatus(1);
        // 使用MD5加密算法对默认密码"123456"进行加密处理，确保密码安全性
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        // 调用save方法保存员工信息到数据库
        save(employee);
    }


    /**
     * 根据员工姓名查询员工信息，并按照更新时间降序排列
     * 此方法用于处理员工信息的分页查询，可以根据姓名模糊匹配员工记录，
     * 并确保查询结果是按照更新时间从新到旧排序的
     *
     * @param pageInfo 分页信息，包含当前页码、每页大小等信息
     * @param name     员工姓名，用于模糊查询
     * @return 返回填充了查询结果的分页信息对象
     */
    @Override
    public Page selectPage(Page pageInfo, String name) {
        // 创建Lambda查询包装器，用于构造查询条件
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();

        // 如果姓名参数不为空也不为空字符串，则添加模糊查询条件
        // 这里使用StringUtil.notNullNorEmpty判断name是否为空或空字符串
        // 如果满足条件，则按照Employee类中的getName方法进行模糊查询
        queryWrapper.like(StringUtil.notNullNorEmpty(name), Employee::getName, name)
                .orderByDesc(Employee::getUpdateTime);  // 添加按照更新时间降序排序的条件

        // 执行分页查询，传入分页信息和查询条件
        page(pageInfo, queryWrapper);

        // 返回填充了查询结果的分页信息对象
        return pageInfo;
    }
}
