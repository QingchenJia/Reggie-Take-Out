package reggietakeout.service.impl;

import ch.qos.logback.core.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import reggietakeout.entity.Dish;
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

    /**
     * 根据名称查询菜品信息，并返回分页结果
     *
     * @param pageInfo 分页信息，包含页码、页大小等
     * @param name     菜品名称，用于模糊查询
     * @return 返回填充了查询结果的分页信息对象
     */
    @Override
    public Page selectPage(Page pageInfo, String name) {
        // 创建Lambda查询包装器，用于后续的条件查询
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        // 根据菜品名称进行模糊查询，仅当名称不为空且不只包含空格时执行查询
        queryWrapper.like(StringUtil.notNullNorEmpty(name), Dish::getName, name);

        // 执行分页查询，结果填充到pageInfo中
        page(pageInfo, queryWrapper);

        // 返回填充了查询结果的分页信息对象
        return pageInfo;
    }
}
