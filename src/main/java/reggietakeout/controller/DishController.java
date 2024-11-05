package reggietakeout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reggietakeout.common.R;
import reggietakeout.dto.DishDto;
import reggietakeout.entity.Dish;
import reggietakeout.service.CategoryService;
import reggietakeout.service.DishFlavorService;
import reggietakeout.service.DishService;

import java.util.List;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 处理新增菜品的请求
     * <p>
     * 该方法接收一个 DishDto 对象作为参数，用于保存菜品信息，包括基本的菜品信息和口味信息
     * 使用 POST 请求方式，因为新增操作通常会创建一个新的资源
     *
     * @param dishDto 包含菜品信息和口味信息的 DishDto 对象，用于保存菜品
     * @return 返回一个表示操作结果的 R<String> 对象，包含操作状态和提示信息
     */
    @PostMapping()
    @Transactional
    public R<String> save(@RequestBody DishDto dishDto) {
        // 记录日志，输出新增菜品的信息，以便于调试和追踪
        log.info("新增菜品，菜品信息：{}", dishDto);

        // 调用 dishService 的 save 方法保存菜品信息
        dishService.save(dishDto);
        // 调用 dishFlavorService 的 insert 方法保存菜品的口味信息
        dishFlavorService.insert(dishDto);

        // 返回成功响应，表示菜品新增成功
        return R.success("新增菜品成功");
    }

    /**
     * 根据页面号和页面大小获取菜品信息列表
     * 此方法首先根据传入的页面号和页面大小创建一个Page对象，然后调用dishService的selectPage方法查询菜品信息
     * 如果查询成功，进一步将查询结果转换为DishDto对象列表，并设置相应的菜品分类名称
     * 最后，将转换后的数据封装到Page对象中返回
     *
     * @param page     页面号，用于指定从哪一页开始查询
     * @param pageSize 页面大小，用于指定每页显示的记录数
     * @param name     菜品名称，用于模糊查询
     * @return 返回一个封装了菜品信息列表的响应对象
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(int page, int pageSize, String name) {
        // 创建Page对象，用于封装分页查询的参数
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        // 调用服务层方法，执行分页查询
        dishService.selectPage(pageInfo, name);

        // 获取查询结果中的记录列表
        List<Dish> dishes = pageInfo.getRecords();

        // 将查询到的Dish对象转换为DishDto对象，并设置菜品分类名称
        List<DishDto> dishDtos = dishes.stream()
                .map(dish -> {
                    DishDto dishDto = new DishDto();
                    // 复制Dish对象的属性到DishDto对象
                    BeanUtils.copyProperties(dish, dishDto);

                    // 根据菜品分类ID查询分类名称，并设置到DishDto对象
                    String categoryName = categoryService.selectById(dish.getCategoryId()).getName();
                    dishDto.setCategoryName(categoryName);

                    return dishDto;
                })
                .toList();

        // 创建一个新的Page对象，用于封装转换后的查询结果
        Page<DishDto> pageResult = new Page<>();
        // 复制分页信息到新的Page对象，但不包括records属性
        BeanUtils.copyProperties(pageInfo, pageResult, "records");

        // 设置转换后的菜品信息列表到新的Page对象
        pageResult.setRecords(dishDtos);

        // 返回封装了查询结果的响应对象
        return R.success(pageResult);
    }
}
