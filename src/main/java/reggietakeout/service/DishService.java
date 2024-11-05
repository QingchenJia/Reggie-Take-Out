package reggietakeout.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import reggietakeout.entity.Dish;

public interface DishService extends IService<Dish> {
    int getCountByCategoryId(Long categoryId);

    Page selectPage(Page pageInfo, String name);
}
