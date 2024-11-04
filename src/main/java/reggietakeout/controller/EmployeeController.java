package reggietakeout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import reggietakeout.common.R;
import reggietakeout.entity.Employee;
import reggietakeout.service.EmployeeService;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param request  HTTP请求对象，用于获取会话信息
     * @param employee 员工对象，包含登录所需的用户名和密码
     * @return 返回登录结果，包括是否成功和相关提示信息
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        // 读取账号密码，并使用MD5摘要算法对其生成哈希值
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));

        // 通过唯一索引username查询结果
        Employee emp = employeeService.selectByUsername(employee.getUsername());

        // 查询结果为空
        if (emp == null)
            return R.error("登陆失败");

        // 密码校对不匹配
        if (!emp.getPassword().equals(password))
            return R.error("登陆失败");

        // 获取账号状态，1为正常，0为禁用
        if (emp.getStatus() == 0)
            return R.error("账号已锁定");

        // 登录成功，服务器存储账号id，保存登陆状态
        request.getSession().setAttribute("employee", emp.getId());
        log.info("登录成功，当前员工id：{}", emp.getId());

        return R.success(emp);
    }

    /**
     * 退出登录
     * <p>
     * 此方法处理用户的退出登录请求，通过移除存储在session中的用户信息来实现注销功能
     * 使用HttpServletRequest对象来访问和操作session
     *
     * @param request 包含用户请求的数据，用于访问session
     * @return 返回一个表示成功退出登录的消息响应
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        // 服务端从session中移除员工id
        request.getSession().removeAttribute("employee");
        return R.success("退出登录");
    }

    /**
     * 保存新增员工信息
     *
     * @param employee 新增员工的信息，以JSON格式封装在请求体中
     * @return 返回保存结果的成功消息
     */
    @PostMapping()
    public R<String> save(@RequestBody Employee employee) {
        // 记录新增员工的日志信息
        log.info("新增员工，员工信息：{}", employee.toString());

        // 调用服务层方法，插入新员工信息到数据库，并传入当前用户ID
        employeeService.insert(employee);

        // 返回成功结果，表示新增员工操作成功
        return R.success("新增员工成功");
    }

    /**
     * 处理员工信息分页查询请求
     *
     * @param page     分页查询的页码
     * @param pageSize 每页显示的记录数
     * @param name     员工姓名，用于模糊查询
     * @return 返回分页查询结果封装在R对象中
     */
    @GetMapping("/page")
    public R<Page<Employee>> page(int page, int pageSize, String name) {
        // 创建分页对象，设置分页参数
        Page<Employee> pageInfo = new Page<>(page, pageSize);

        // 调用服务层方法，查询分页数据
        Page<Employee> pageResult = employeeService.selectPage(pageInfo, name);

        // 返回分页结果
        return R.success(pageResult);
    }

    /**
     * 更新员工信息的接口
     * 使用PUT请求映射来更新数据库中的员工记录
     *
     * @param employee 包含更新后员工信息的请求体
     * @return 返回一个响应对象，包含操作结果的字符串消息
     */
    @PutMapping()
    public R<String> update(@RequestBody Employee employee) {
        // 记录更新员工的日志信息
        log.info("更新员工，员工信息：{}", employee.toString());

        // 调用服务层方法更新员工信息
        employeeService.updateById(employee);
        // 返回成功响应，表示员工信息修改成功
        return R.success("员工信息修改成功");
    }

    /**
     * 根据员工ID获取员工信息
     *
     * @param id 员工ID，通过URL路径传入
     * @return 返回一个封装了员工信息的响应对象如果未找到对应员工，则返回错误信息
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        // 调用服务层方法，根据ID查询员工
        Employee employee = employeeService.getById(id);

        // 判断员工是否为空
        if (employee != null) {
            // 如果员工不为空，返回成功响应，包含员工信息
            return R.success(employee);
        }
        // 如果员工为空，返回错误响应，包含错误信息
        return R.error("没有查询到对应员工");
    }
}
