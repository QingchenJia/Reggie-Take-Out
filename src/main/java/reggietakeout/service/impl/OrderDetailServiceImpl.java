package reggietakeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import reggietakeout.entity.OrderDetail;
import reggietakeout.mapper.OrderDetailMapper;
import reggietakeout.service.OrderDetailService;

import java.util.List;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
    /**
     * 根据订单ID选择订单详情
     *
     * @param orderId 订单ID，用于查询订单详情
     * @return 返回一个订单详情列表，如果找不到相关订单详情，则返回空列表
     */
    @Override
    public List<OrderDetail> selectByOrderId(Long orderId) {
        // 创建Lambda查询包装器以构建查询条件
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        // 设置查询条件为OrderDetail的orderId字段等于传入的orderId参数
        queryWrapper.eq(OrderDetail::getOrderId, orderId);

        // 执行查询并返回结果列表
        return list(queryWrapper);
    }
}
