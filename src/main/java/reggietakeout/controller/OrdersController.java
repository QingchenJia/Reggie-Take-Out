package reggietakeout.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reggietakeout.common.BaseContext;
import reggietakeout.common.R;
import reggietakeout.entity.AddressBook;
import reggietakeout.entity.OrderDetail;
import reggietakeout.entity.Orders;
import reggietakeout.entity.ShoppingCart;
import reggietakeout.service.AddressBookService;
import reggietakeout.service.OrderDetailService;
import reggietakeout.service.OrdersService;
import reggietakeout.service.ShoppingCartService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 处理订单提交请求
     * <p>
     * 该方法主要用于接收用户提交的订单信息，进行订单创建，并保存订单详情同时清空购物车
     * 使用@PostMapping注解限定该方法响应POST请求，表示订单的创建
     *
     * @param orders 用户提交的订单对象，包含订单的相关信息
     * @return 返回一个封装了成功消息的响应对象
     * @Transactional注解表明该方法中的操作需要事务管理，确保数据一致性
     */
    @PostMapping("/submit")
    @Transactional
    public R<String> submit(@RequestBody Orders orders) {
        // 记录用户下单日志
        log.info("用户下单：{}", orders);

        // 获取当前用户ID
        Long userId = BaseContext.getCurrentId();
        // 根据用户选择的地址簿ID获取地址簿信息
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());

        // 获取当前用户购物车中的所有商品
        List<ShoppingCart> shoppingCarts = shoppingCartService.selectByUserId(userId);

        // 将购物车中的商品转换为订单详情列表
        List<OrderDetail> orderDetails = shoppingCarts.stream().map(shoppingCart -> {
            // 创建一个新的订单详情对象
            OrderDetail orderDetail = new OrderDetail();
            // 将购物车中的商品信息复制到订单详情中，但不包括ID
            BeanUtils.copyProperties(shoppingCart, orderDetail, "id");

            return orderDetail;
        }).toList();

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 计算订单总金额
        for (OrderDetail orderDetail : orderDetails) {
            totalAmount = totalAmount.add(orderDetail.getAmount().multiply(BigDecimal.valueOf(orderDetail.getNumber())));
        }

        // 设置订单总金额
        orders.setAmount(totalAmount);

        // 插入订单，返回订单ID
        Long orderId = ordersService.insertOrders(orders, addressBook);

        // 设置每个订单详情的订单ID
        orderDetails.forEach(orderDetail -> orderDetail.setOrderId(orderId));

        // 批量保存订单详情
        orderDetailService.saveBatch(orderDetails);

        // 用户下单后，清空购物车
        shoppingCartService.deleteByUserId(userId);
        // 返回成功消息
        return R.success("下单成功");
    }
}
