package reggietakeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import reggietakeout.dto.SetmealDto;
import reggietakeout.entity.Setmeal;
import reggietakeout.mapper.SetmealMapper;
import reggietakeout.service.SetmealService;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    /**
     * 根据类别ID获取套餐数量
     * <p>
     * 此方法用于查询特定类别下的套餐数量通过构建一个Lambda查询包装器来筛选出符合类别ID的套餐，
     * 然后统计这些套餐的数量
     *
     * @param categoryId 类别ID，用于指定需要统计套餐数量的类别
     * @return 返回指定类别下的套餐数量
     */
    @Override
    public int getCountByCategoryId(Long categoryId) {
        // 创建Lambda查询包装器，用于后续的条件查询
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        // 设置查询条件：根据类别ID等于传入的categoryId
        queryWrapper.eq(Setmeal::getCategoryId, categoryId);

        // 使用Lambda查询包装器进行查询，并将结果转换为整数返回
        return (int) count(queryWrapper);
    }

    /**
     * 插入套餐信息
     * <p>
     * 该方法接收一个套餐DTO（数据传输对象），将其属性复制到一个套餐实体对象中，
     * 然后保存该实体对象到数据库，并返回生成的套餐ID
     *
     * @param setmealDto 套餐DTO，包含要插入的套餐的相关信息
     * @return 插入成功后生成的套餐ID
     */
    @Override
    public Long insertSetmeal(SetmealDto setmealDto) {
        // 创建一个新的套餐实体对象
        Setmeal setmeal = new Setmeal();
        // 将套餐DTO中的属性复制到套餐实体对象中
        BeanUtils.copyProperties(setmealDto, setmeal);

        // 保存套餐实体对象到数据库
        save(setmeal);

        // 返回生成的套餐ID
        return setmeal.getId();
    }
}
