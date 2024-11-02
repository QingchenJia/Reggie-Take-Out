package reggietakeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import reggietakeout.entity.Category;
import reggietakeout.exception.NameRepeatException;
import reggietakeout.mapper.CategoryMapper;
import reggietakeout.service.CategoryService;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    /**
     * 插入一个新的分类
     * <p>
     * 此方法首先检查传入的分类名称是否已存在于数据库中如果存在，抛出NameRepeatException异常；
     * 如果不存在，则将该分类保存到数据库中
     *
     * @param category 要插入的分类对象
     * @throws NameRepeatException 如果分类名称已存在
     */
    @Override
    public void insert(Category category) {
        // 获取分类名称
        String name = category.getName();

        // 检查分类名称是否已存在，如果存在则抛出异常
        if (selectByName(name) != null)
            throw new NameRepeatException("分类名称已存在");

        // 保存分类到数据库
        save(category);
    }

    /**
     * 根据名称选择类别
     * <p>
     * 此方法使用LambdaQueryWrapper来构造查询条件，以实现根据类别名称查询类别信息的功能
     * 它首先创建一个LambdaQueryWrapper对象，然后设置查询条件为类别名称等于输入的name
     * 最后，使用getOne方法执行查询并返回单个类别对象
     *
     * @param name 要查询的类别名称
     * @return 返回查询到的类别对象，如果没有查询到则返回null
     */
    @Override
    public Category selectByName(String name) {
        // 创建LambdaQueryWrapper对象，用于构建查询条件
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 设置查询条件为类别名称等于输入的name
        queryWrapper.eq(Category::getName, name);

        // 执行查询并返回单个类别对象
        return getOne(queryWrapper);
    }

    /**
     * 根据类别名称查询并返回排序后的类别页面信息
     * <p>
     * 此方法使用LambdaQueryWrapper构建查询条件，以实现对类别名称的精确匹配和根据类别排序字段的升序排列
     * 它首先检查传入的名称是否非空且不为空字符串如果条件满足，则使用该名称查询数据库，并按排序字段升序排列结果
     * 最后，将查询结果填充到传入的Page对象中，并返回该对象
     *
     * @param pageInfo 分页信息，包括当前页码、每页记录数等信息，同时也将承载查询结果返回
     * @return 返回填充了查询结果的Page对象，包含符合查询条件的类别信息列表
     */
    @Override
    public Page selectPage(Page pageInfo) {
        // 创建LambdaQueryWrapper实例，用于构建查询条件
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        // 添加查询条件：当name非空且不为空字符串时，按名称查询，并按sort字段升序排列
        queryWrapper.orderByAsc(Category::getSort);

        // 执行分页查询，并将结果填充到pageInfo中
        page(pageInfo, queryWrapper);

        // 返回填充了查询结果的pageInfo对象
        return pageInfo;
    }
}
