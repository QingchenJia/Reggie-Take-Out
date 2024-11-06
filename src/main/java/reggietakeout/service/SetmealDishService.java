package reggietakeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import reggietakeout.dto.SetmealDto;
import reggietakeout.entity.SetmealDish;

public interface SetmealDishService extends IService<SetmealDish> {
    void insertSetmealDish(SetmealDto setmealDto);
}
