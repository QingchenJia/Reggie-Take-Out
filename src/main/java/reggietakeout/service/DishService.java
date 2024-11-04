package reggietakeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import reggietakeout.dto.DishDto;
import reggietakeout.entity.Dish;

public interface DishService extends IService<Dish> {
    int getCountByCategoryId(Long categoryId);
}
