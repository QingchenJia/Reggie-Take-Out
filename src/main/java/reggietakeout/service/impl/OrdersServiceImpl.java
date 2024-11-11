package reggietakeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import reggietakeout.common.BaseContext;
import reggietakeout.entity.AddressBook;
import reggietakeout.entity.Orders;
import reggietakeout.mapper.OrdersMapper;
import reggietakeout.service.OrdersService;

import java.time.LocalDateTime;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    /**
     * 插入订单信息
     * <p>
     * 此方法用于将订单信息以及关联的地址信息插入数据库它首先构建订单对象，
     * 设置与地址簿关联的信息，如电话、地址详情、收货人等，然后保存订单对象，
     * 并返回订单ID
     *
     * @param orders      订单对象，包含订单的基本信息
     * @param addressBook 地址簿对象，包含收货地址的相关信息
     * @return 返回新插入订单的ID
     */
    @Override
    public Long insertOrders(Orders orders, AddressBook addressBook) {
        // 构建订单对象，设置收货信息和用户ID，以及订单时间和结算时间

        orders.setPhone(addressBook.getPhone()); // 设置收货电话
        orders.setAddress(addressBook.getDetail()); // 设置收货地址详情
        orders.setConsignee(addressBook.getConsignee()); // 设置收货人
        orders.setUserId(BaseContext.getCurrentId());// 设置用户ID
        orders.setOrderTime(LocalDateTime.now());// 设置订单时间
        orders.setCheckoutTime(LocalDateTime.now()); // 设置结算时间

        // 保存构建好的订单对象到数据库
        save(orders);

        // 返回新插入订单的ID
        return orders.getId();
    }
}
