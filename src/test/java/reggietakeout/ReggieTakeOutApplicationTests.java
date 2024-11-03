package reggietakeout;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reggietakeout.entity.Dish;
import reggietakeout.entity.Employee;
import reggietakeout.service.DishService;
import reggietakeout.service.EmployeeService;

import java.util.List;

@SpringBootTest
class ReggieTakeOutApplicationTests {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private DishService dishService;

    @Test
    void contextLoads() {
    }

    @Test
    void testSelectPage() {
        Page<Employee> pageInfo = new Page<>(1, 10);
        Page<Employee> pageResult = employeeService.selectPage(pageInfo, "A");
        System.out.println(pageResult.getRecords());
    }

    @Test
    void filedNameAutoFill() {
        Employee employee = new Employee();
        employee.setUsername("eric");
        employee.setName("Eric");
        employee.setSex("1");
        employee.setPhone("15745693652");
        employee.setIdNumber("512632199904211457");

        employee.setStatus(1);

        employeeService.save(employee);
    }

    @Test
    void testGetCountByCategoryId() {
        System.out.println(dishService.getCountByCategoryId(1397844263642378242L));
    }

    @Test
    void testQueryDish() {
        List<Dish> list = dishService.list();
        for (Dish dish : list) {
            System.out.println(dish);
        }
    }
}
