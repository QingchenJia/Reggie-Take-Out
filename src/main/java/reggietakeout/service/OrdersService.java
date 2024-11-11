package reggietakeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import reggietakeout.entity.AddressBook;
import reggietakeout.entity.Orders;

public interface OrdersService extends IService<Orders> {
    Long insertOrders(Orders orders, AddressBook addressBook);
}
