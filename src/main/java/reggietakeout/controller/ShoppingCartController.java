package reggietakeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reggietakeout.common.BaseContext;
import reggietakeout.common.R;
import reggietakeout.entity.ShoppingCart;
import reggietakeout.service.ShoppingCartService;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 获取当前用户的购物车列表
     * <p>
     * 此方法首先获取当前用户的ID，然后构造查询条件，查询该用户的所有购物车项
     * 使用LambdaQueryWrapper来构建查询条件，以确保查询结果仅限于当前用户
     *
     * @return 返回一个响应对象，包含用户购物车列表的成功查询结果
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        // 获取当前用户的ID
        Long userId = BaseContext.getCurrentId();

        // 构造查询条件，用于查询当前用户的所有购物车项
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);

        // 查询用户购物车列表并返回成功响应
        return R.success(shoppingCartService.list(queryWrapper));
    }

    /**
     * 处理购物车添加商品的请求
     *
     * @param shoppingCart 通过请求体接收的购物车信息，包含要添加的商品详情
     * @return 返回一个响应对象，表示添加操作的结果
     */
    @PostMapping("/add")
    public R<String> add(@RequestBody ShoppingCart shoppingCart) {
        // 记录日志，输出购物车信息，用于调试和追踪
        log.info("购物车数据:{}", shoppingCart);

        // 设置当前用户的ID到购物车对象，以便关联用户和购物车
        shoppingCart.setUserId(BaseContext.getCurrentId());

        // 调用服务层方法，将购物车对象插入到数据库中
        shoppingCartService.insertShoppingCart(shoppingCart);

        // 返回成功响应，通知客户端商品已成功添加到购物车
        return R.success("添加成功");
    }

    /**
     * 处理减少购物车中菜品或套餐数量的请求
     *
     * @param map 包含菜品ID（dishId）和套餐ID（setmealId）的映射
     * @return 返回表示操作成功的响应对象
     */
    @PostMapping("/sub")
    public R<String> reduce(@RequestBody Map map) {
        // 记录减少菜品或套餐的日志信息
        log.info("减少一份菜品或套餐：{}", map);

        // 从请求体中获取菜品ID和套餐ID，并转换为Long类型
        Long dishId = map.get("dishId") == null ? null : Long.parseLong(map.get("dishId").toString());
        Long setmealId = map.get("setmealId") == null ? null : Long.parseLong(map.get("setmealId").toString());
        // 获取当前用户ID
        Long userId = BaseContext.getCurrentId();

        // 调用服务层方法，减少购物车中的菜品或套餐数量
        shoppingCartService.deleteShoppingCart(dishId, setmealId, userId);

        // 返回操作成功的信息
        return R.success("减少成功");
    }

    /**
     * 清空当前用户的购物车
     * <p>
     * 该方法通过删除当前用户在购物车中的所有商品来实现清空购物车的功能
     * 它首先创建一个LambdaUpdateWrapper对象，用于构建更新条件，然后设置更新条件为用户ID等于当前用户的ID
     * 最后调用shoppingCartService的remove方法，根据更新条件删除购物车中的商品
     *
     * @return 返回一个R对象，表示操作结果如果成功，将返回成功信息和HTTP状态码200
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        // 创建LambdaUpdateWrapper对象，用于构建更新条件
        LambdaUpdateWrapper<ShoppingCart> updateWrapper = new LambdaUpdateWrapper<>();
        // 设置更新条件为用户ID等于当前用户的ID
        updateWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        // 根据更新条件删除购物车中的商品
        shoppingCartService.remove(updateWrapper);

        // 返回成功信息
        return R.success("清空购物车成功");
    }
}
