package reggietakeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import reggietakeout.entity.Orders;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
