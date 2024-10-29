package reggietakeout.controller;

import jakarta.servlet.http.HttpServletRequest;
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
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param request
     * @param employee
     * @return
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
        return R.success(emp);
    }
}
