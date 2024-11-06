package reggietakeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import reggietakeout.dto.SetmealDto;
import reggietakeout.entity.SetmealDish;

import java.util.List;

public interface SetmealDishService extends IService<SetmealDish> {
    void insertSetmealDish(SetmealDto setmealDto);

    List<SetmealDish> selectBySetmealId(Long setmealId);
}
