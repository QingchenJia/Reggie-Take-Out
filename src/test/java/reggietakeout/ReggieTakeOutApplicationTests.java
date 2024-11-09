package reggietakeout;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reggietakeout.entity.Dish;
import reggietakeout.entity.Employee;
import reggietakeout.entity.ShoppingCart;
import reggietakeout.service.DishService;
import reggietakeout.service.EmployeeService;
import reggietakeout.service.ShoppingCartService;
import reggietakeout.utils.CaptchaUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class ReggieTakeOutApplicationTests {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private DishService dishService;
    @Autowired
    private ShoppingCartService shoppingCartService;

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

    @Test
    void testQueryDishPage() {
        Page<Dish> pageInfo = new Page<>(1L, 10L);

        dishService.selectPage(pageInfo, null);

        pageInfo.getRecords().forEach(System.out::println);
    }

    @Test
    void testString2LongList() {
        Arrays.stream("1853439708533260290,1413385247889891330,1413342036832100354,1413384757047271425".split(","))
                .map(id -> Long.parseLong(id))
                .toList()
                .forEach(System.out::println);
    }

    @Test
    void testSmallMessageCodeGenerate() {
        String s = CaptchaUtils.generateCaptcha();
        System.out.println(s);
    }

    @Test
    void testAddShoppingCartItem() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(1854447513386000385L);
        shoppingCart.setDishId(1853439708533260290L);
        shoppingCart.setDishFlavor("少糖");
        shoppingCart.setAmount(new BigDecimal(1500));
        shoppingCart.setImage("85550581-d844-4c76-bf6c-0d890da8eb01.jpg");
        shoppingCart.setName("程序员猫猫");

        shoppingCartService.insertShoppingCart(shoppingCart);
    }
}
