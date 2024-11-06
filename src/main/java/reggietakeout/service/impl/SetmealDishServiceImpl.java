package reggietakeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import reggietakeout.dto.SetmealDto;
import reggietakeout.entity.SetmealDish;
import reggietakeout.mapper.SetmealDishMapper;
import reggietakeout.service.SetmealDishService;

import java.util.List;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
    /**
     * 插入套餐菜品关联数据
     * <p>
     * 此方法主要用于将套餐详情中的菜品信息批量插入数据库
     * 它首先将套餐详情中的每个菜品的套餐ID设置为当前套餐的ID，
     * 然后批量保存这些菜品信息到数据库中
     *
     * @param setmealDto 套餐详情对象，包含套餐的基本信息及其包含的菜品列表
     */
    @Override
    public void insertSetmealDish(SetmealDto setmealDto) {
        // 获取套餐包含的菜品列表
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        // 为每个菜品设置套餐ID
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealDto.getId()));

        // 批量保存菜品信息
        saveBatch(setmealDishes);
    }

    /**
     * 根据套餐ID选择套餐菜品
     *
     * @param setmealId 套餐ID，用于查询与之关联的套餐菜品信息
     * @return 返回一个包含套餐菜品对象的列表，这些菜品属于指定的套餐
     */
    @Override
    public List<SetmealDish> selectBySetmealId(Long setmealId) {
        // 创建一个Lambda查询包装器，用于构建查询条件
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        // 设置查询条件，筛选出套餐ID与参数setmealId相等的套餐菜品
        queryWrapper.eq(SetmealDish::getSetmealId, setmealId);

        // 执行查询并返回结果列表
        return list(queryWrapper);
    }
}
