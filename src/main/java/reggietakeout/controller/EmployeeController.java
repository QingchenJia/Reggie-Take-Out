package reggietakeout.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
     * @param request  HTTP请求对象，用于获取当前会话的用户ID
     * @param employee 新增员工的信息，以JSON格式封装在请求体中
     * @return 返回保存结果的成功消息
     */
    @PostMapping()
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        // 记录新增员工的日志信息
        log.info("新增员工，员工信息：{}", employee.toString());

        // 从会话中获取当前用户的ID，用于记录是谁添加了新员工
        Long userId = (Long) request.getSession().getAttribute("employee");

        // 调用服务层方法，插入新员工信息到数据库，并传入当前用户ID
        employeeService.insert(employee, userId);

        // 返回成功结果，表示新增员工操作成功
        return R.success("新增员工成功");
    }
}
