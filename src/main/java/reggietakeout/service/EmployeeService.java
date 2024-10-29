package reggietakeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import reggietakeout.entity.Employee;

public interface EmployeeService extends IService<Employee> {
    Employee selectByUsername(String username);
}
