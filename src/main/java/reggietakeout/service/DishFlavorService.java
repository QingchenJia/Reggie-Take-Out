package reggietakeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import reggietakeout.dto.DishDto;
import reggietakeout.entity.DishFlavor;

import java.util.List;

public interface DishFlavorService extends IService<DishFlavor> {
    void insert(DishDto dishDto);

    void updateDishFlavor(DishDto dishDto);

    List<DishFlavor> selectByDishId(Long dishId);
}
