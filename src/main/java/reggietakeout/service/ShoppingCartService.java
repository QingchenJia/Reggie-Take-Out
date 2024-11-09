package reggietakeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import reggietakeout.entity.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {
    void insertShoppingCart(ShoppingCart shoppingCart);

    void deleteShoppingCart(Long dishId, Long setmealId, Long userId);
}
