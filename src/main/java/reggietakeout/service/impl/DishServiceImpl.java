package reggietakeout.service.impl;

import ch.qos.logback.core.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import reggietakeout.entity.Dish;
import reggietakeout.entity.DishFlavor;
import reggietakeout.mapper.DishMapper;
import reggietakeout.service.DishService;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    /**
     * 根据类别ID获取菜品数量
     * <p>
     * 此方法用于统计属于特定类别的菜品数量通过构造一个Lambda查询包装器，
     * 指定查询条件为类别ID等于传入的categoryId参数，然后使用count方法查询数量
     *
     * @param categoryId 类别ID，用于指定需要统计的菜品类别
     * @return 返回指定类别下的菜品数量
     */
    @Override
    public int getCountByCategoryId(Long categoryId) {
        // 创建Lambda查询包装器，用于构建查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 设置查询条件：类别ID等于传入的categoryId
        queryWrapper.eq(Dish::getCategoryId, categoryId);

        // 执行查询并返回结果，将结果转换为int类型
        return (int) count(queryWrapper);
    }

    @Override
    public Page selectPage(Page pageInfo, String name) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtil.notNullNorEmpty(name), Dish::getName, name);

        page(pageInfo, queryWrapper);

        return pageInfo;
    }
}
