package reggietakeout.dto;

import lombok.Data;
import reggietakeout.entity.OrderDetail;
import reggietakeout.entity.Orders;

import java.util.List;

@Data
public class OrdersDto extends Orders {
    private List<OrderDetail> orderDetails;
}
