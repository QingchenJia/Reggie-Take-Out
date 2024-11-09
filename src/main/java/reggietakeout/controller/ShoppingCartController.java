package reggietakeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reggietakeout.common.BaseContext;
import reggietakeout.common.R;
import reggietakeout.entity.ShoppingCart;
import reggietakeout.service.ShoppingCartService;

import java.util.List;

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
        // 设置当前用户的ID到购物车对象，以便关联用户和购物车
        shoppingCart.setUserId(BaseContext.getCurrentId());

        // 调用服务层方法，将购物车对象插入到数据库中
        shoppingCartService.insertShoppingCart(shoppingCart);

        // 返回成功响应，通知客户端商品已成功添加到购物车
        return R.success("添加成功");
    }
}
