package reggietakeout.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import reggietakeout.entity.Employee;

public interface EmployeeService extends IService<Employee> {
    Employee selectByUsername(String username);

    void insert(Employee employee, Long userId);

    Page selectPage(Page pageInfo, String name);
}
