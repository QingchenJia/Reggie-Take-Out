package reggietakeout.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import reggietakeout.dto.SetmealDto;
import reggietakeout.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {
    int getCountByCategoryId(Long categoryId);

    Long insertSetmeal(SetmealDto setmealDto);

    Page selectPage(Page pageInfo, String name);
}
