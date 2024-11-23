package reggietakeout;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import reggietakeout.entity.*;
import reggietakeout.service.*;
import reggietakeout.utils.CaptchaUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Slf4j
class ReggieTakeOutApplicationTests {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private DishService dishService;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private RedisTemplate redisTemplate;

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
        Arrays.stream("1853439708533260290,1413385247889891330,1413342036832100354,1413384757047271425".split(",")).map(id -> Long.parseLong(id)).toList().forEach(System.out::println);
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

    @Test
    void testReduceShoppingCartNumber() {
        shoppingCartService.deleteShoppingCart(1853439708533260290L, null, 1854447513386000385L);
    }

    @Test
    void testOrdersSubmit() {
        Orders orders = new Orders();
        orders.setAddressBookId(1417414526093082626L);
        orders.setPayMethod(1);
        orders.setRemark("记得下次继续点我呀");

        // 记录用户下单日志
        log.info("用户下单：{}", orders);

        // 获取当前用户ID
        Long userId = 1854447513386000385L;
        // 根据用户选择的地址簿ID获取地址簿信息
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());

        // 获取当前用户购物车中的所有商品
        List<ShoppingCart> shoppingCarts = shoppingCartService.selectByUserId(userId);

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 将购物车中的商品转换为订单详情列表
        List<OrderDetail> orderDetails = shoppingCarts.stream().map(shoppingCart -> {
            // 创建一个新的订单详情对象
            OrderDetail orderDetail = new OrderDetail();
            // 将购物车中的商品信息复制到订单详情中，但不包括ID
            BeanUtils.copyProperties(shoppingCart, orderDetail, "id");

            return orderDetail;
        }).toList();

        for (OrderDetail orderDetail : orderDetails) {
            totalAmount = totalAmount.add(orderDetail.getAmount().multiply(BigDecimal.valueOf(orderDetail.getNumber())));
        }

        orders.setAmount(totalAmount);

        // 插入订单，返回订单ID
        Long orderId = ordersService.insertOrders(orders, addressBook);

        orderDetails.forEach(orderDetail -> orderDetail.setOrderId(orderId));

        // 批量保存订单详情
        orderDetailService.saveBatch(orderDetails);

        // 用户下单后，清空购物车
        shoppingCartService.deleteByUserId(userId);
    }

    @Test
    void testRedisUse() {
        ValueOperations value = redisTemplate.opsForValue();
        value.set("name", "eric");
        System.out.println(value.get("name"));
    }
}
