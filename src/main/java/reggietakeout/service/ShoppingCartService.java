package reggietakeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import reggietakeout.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService extends IService<ShoppingCart> {
    void insertShoppingCart(ShoppingCart shoppingCart);

    void deleteShoppingCart(Long dishId, Long setmealId, Long userId);

    void deleteByUserId(Long userId);

    List<ShoppingCart> selectByUserId(Long userId);
}
