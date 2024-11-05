package reggietakeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import reggietakeout.dto.DishDto;
import reggietakeout.entity.DishFlavor;
import reggietakeout.mapper.DishFlavorMapper;
import reggietakeout.service.DishFlavorService;

import java.util.List;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
    /**
     * 重写插入方法，用于处理菜肴及其风味的批量保存
     *
     * @param dishDto 菜肴数据传输对象，包含菜肴信息及其对应的风味列表
     */
    @Override
    public void insert(DishDto dishDto) {
        // 获取菜肴ID
        Long dishId = dishDto.getId();
        // 获取菜肴风味列表
        List<DishFlavor> flavors = dishDto.getFlavors();

        // 遍历风味列表，为每个风味设置菜肴ID
        flavors.forEach(flavor -> flavor.setDishId(dishId));

        // 批量保存风味列表
        saveBatch(flavors);
    }

    /**
     * 更新菜品口味信息
     * 此方法首先根据菜品ID删除现有的口味信息，然后插入新的口味信息
     *
     * @param dishDto 包含菜品及其新口味信息的传输对象
     */
    @Override
    public void updateDishFlavor(DishDto dishDto) {
        // 创建查询包装器，用于后续的条件查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        // 设置查询条件：根据菜品ID匹配
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());

        // 删除现有口味信息，为插入新信息做准备
        remove(queryWrapper);

        // 插入新的口味信息
        insert(dishDto);
    }

    /**
     * 根据菜品ID查询菜品口味信息
     * <p>
     * 此方法通过Lambda查询构造器，根据给定的菜品ID查询与之关联的所有口味信息
     * 使用Lambda表达式可以提高查询的效率和可读性
     *
     * @param dishId 菜品ID，用于查询与之关联的口味信息
     * @return 返回一个包含DishFlavor对象的列表，表示与指定菜品ID关联的所有口味信息
     */
    @Override
    public List<DishFlavor> selectByDishId(Long dishId) {
        // 创建Lambda查询构造器，并设置查询条件为菜品ID等于给定的dishId
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishId);

        // 执行查询并返回结果列表
        return list(queryWrapper);
    }
}
