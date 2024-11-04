package reggietakeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import reggietakeout.dto.DishDto;
import reggietakeout.entity.DishFlavor;

public interface DishFlavorService extends IService<DishFlavor> {
    void insert(DishDto dishDto);
}
