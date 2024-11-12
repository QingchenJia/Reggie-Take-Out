package reggietakeout.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import reggietakeout.dto.SetmealDto;
import reggietakeout.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    int getCountByCategoryId(Long categoryId);

    Long insertSetmeal(SetmealDto setmealDto);

    Page selectPage(Page pageInfo, String name);

    List<Setmeal> selectByCategoryId(Long categoryId, Integer status);
}
