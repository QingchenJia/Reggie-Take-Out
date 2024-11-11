package reggietakeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import reggietakeout.entity.ShoppingCart;
import reggietakeout.mapper.ShoppingCartMapper;
import reggietakeout.service.ShoppingCartService;

import java.util.List;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    /**
     * 插入购物车项
     * 如果购物车项已存在，则增加数量；否则，创建新的购物车项
     *
     * @param shoppingCart 购物车对象，包含用户ID、菜品ID、套餐ID和数量等信息
     */
    @Override
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

    /**
     * 删除购物车中的商品
     * 此方法用于从购物车中删除指定的菜品或套餐当菜品或套餐的数量为1时，直接从数据库中移除该购物车项；
     * 否则，将该菜品或套餐的数量减1并更新数据库
     *
     * @param dishId    菜品ID，用于指定要删除的菜品如果为null，则不考虑菜品
     * @param setmealId 套餐ID，用于指定要删除的套餐如果为null，则不考虑套餐
     * @param userId    用户ID，用于确定用户的购物车
     */
    @Override
    public void deleteShoppingCart(Long dishId, Long setmealId, Long userId) {
        // 创建查询条件构造器
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        // 设置查询条件：根据用户ID，菜品ID和套餐ID查询购物车项
        queryWrapper.eq(ShoppingCart::getUserId, userId)
                .eq(dishId != null, ShoppingCart::getDishId, dishId)
                .eq(setmealId != null, ShoppingCart::getSetmealId, setmealId);

        // 根据查询条件从数据库中获取购物车项
        ShoppingCart shoppingCartdb = getOne(queryWrapper);

        // 判断购物车项中的菜品或套餐数量
        if (shoppingCartdb.getNumber() == 1) {
            // 如果数量为1，直接删除购物车项
            remove(queryWrapper);
        } else {
            // 如果数量大于1，减少数量并更新购物车项
            Integer number = shoppingCartdb.getNumber() - 1;
            shoppingCartdb.setNumber(number);
            updateById(shoppingCartdb);
        }
    }

    /**
     * 根据用户ID删除购物车信息
     *
     * @param userId 用户ID，用于标识需要删除的购物车信息的用户
     */
    @Override
    public void deleteByUserId(Long userId) {
        // 创建Lambda查询包装器，用于构建查询条件
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        // 设置查询条件，根据用户ID进行等值查询
        queryWrapper.eq(ShoppingCart::getUserId, userId);

        // 执行删除操作，传入查询条件
        remove(queryWrapper);
    }

    /**
     * 根据用户ID选择购物车列表
     *
     * @param userId 用户ID，用于查询与用户关联的购物车信息
     * @return 返回与用户ID关联的购物车列表
     */
    @Override
    public List<ShoppingCart> selectByUserId(Long userId) {
        // 创建Lambda查询包装器，用于构建查询条件
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        // 设置查询条件，等同于SQL中的WHERE user_id = ?
        queryWrapper.eq(ShoppingCart::getUserId, userId);

        // 执行查询并返回结果列表
        return list(queryWrapper);
    }
}
