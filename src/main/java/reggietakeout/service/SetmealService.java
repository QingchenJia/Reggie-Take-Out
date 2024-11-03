package reggietakeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import reggietakeout.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {
    int getCountByCategoryId(Long categoryId);
}
