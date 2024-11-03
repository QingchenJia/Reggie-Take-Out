package reggietakeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import reggietakeout.entity.Dish;

public interface DishService extends IService<Dish> {
    int getCountByCategoryId(Long categoryId);
}
