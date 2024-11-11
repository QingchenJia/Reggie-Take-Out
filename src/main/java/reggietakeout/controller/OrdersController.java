package reggietakeout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reggietakeout.common.BaseContext;
import reggietakeout.common.R;
import reggietakeout.dto.OrdersDto;
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

    /**
     * 处理用户页面的分页查询请求
     *
     * @param page     当前页码，用于指定从哪一页开始查询
     * @param pageSize 每页记录数，用于限制每页显示的数据量
     * @return 返回一个封装了分页信息的对象，包含订单数据
     */
    @GetMapping("/userPage")
    public R<Page<OrdersDto>> page(int page, int pageSize) {
        // 创建一个Page对象，用于存储分页查询的结果
        Page<Orders> pageInfo = new Page<>(page, pageSize);

        // 调用服务层方法执行分页查询
        ordersService.selectPage(pageInfo);

        // 获取查询结果中的订单记录列表
        List<Orders> orderss = pageInfo.getRecords();

        // 将订单记录转换为DTO形式，并关联查询订单详情
        List<OrdersDto> ordersDtos = orderss.stream()
                .map(orders -> {
                    OrdersDto ordersDto = new OrdersDto();
                    BeanUtils.copyProperties(orders, ordersDto);

                    // 根据订单ID查询订单详情
                    Long ordersId = orders.getId();
                    List<OrderDetail> orderDetails = orderDetailService.selectByOrderId(ordersId);

                    ordersDto.setOrderDetails(orderDetails);

                    return ordersDto;
                })
                .toList();

        // 创建一个新的Page对象用于存储转换后的DTO数据
        Page<OrdersDto> pageResult = new Page<>();
        BeanUtils.copyProperties(pageInfo, pageResult, "records");

        // 设置转换后的订单DTO列表到分页对象中
        pageResult.setRecords(ordersDtos);

        // 返回查询结果，封装在R对象中表示成功
        return R.success(pageResult);
    }
}
