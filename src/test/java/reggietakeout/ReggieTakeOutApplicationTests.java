package reggietakeout;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reggietakeout.entity.Employee;
import reggietakeout.service.EmployeeService;

@SpringBootTest
class ReggieTakeOutApplicationTests {
    @Autowired
    private EmployeeService employeeService;

    @Test
    void contextLoads() {
    }

    @Test
    void testSelectPage() {
        Page<Employee> pageInfo = new Page<>(1, 10);
        Page<Employee> pageResult = employeeService.selectPage(pageInfo,"A");
        System.out.println(pageResult.getRecords());
    }
}
