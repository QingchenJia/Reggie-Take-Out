package reggietakeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reggietakeout.entity.ShoppingCart;
import reggietakeout.mapper.ShoppingCartMapper;
import reggietakeout.service.ShoppingCartService;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    /**
     * 插入购物车项
     * 如果购物车项已存在，则增加数量；否则，创建新的购物车项
     *
     * @param shoppingCart 购物车对象，包含用户ID、菜品ID、套餐ID和数量等信息
     */
    @Override
    @Transactional
    public void insertShoppingCart(ShoppingCart shoppingCart) {
        // 获取菜品ID和套餐ID
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();

        // 创建查询条件，根据用户ID、菜品ID或套餐ID查询购物车中是否已存在该项
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId())
                .eq(dishId != null, ShoppingCart::getDishId, dishId)
                .eq(setmealId != null, ShoppingCart::getSetmealId, setmealId);

        // 执行查询，获取购物车中的现有项
        ShoppingCart shoppingCartdb = getOne(queryWrapper);

        // 如果购物车项已存在，则增加数量
        if (shoppingCartdb != null) {
            Integer number = shoppingCartdb.getNumber() + 1;
            shoppingCartdb.setNumber(number);
            updateById(shoppingCartdb);
        } else {
            // 如果购物车项不存在，则设置数量为1，并保存到数据库
            shoppingCart.setNumber(1);
            save(shoppingCart);
        }
    }
}
