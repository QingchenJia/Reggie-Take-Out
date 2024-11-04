package reggietakeout.service.impl;

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
}
