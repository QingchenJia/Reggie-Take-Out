package reggietakeout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reggietakeout.common.R;
import reggietakeout.dto.DishDto;
import reggietakeout.entity.Dish;
import reggietakeout.entity.DishFlavor;
import reggietakeout.service.CategoryService;
import reggietakeout.service.DishFlavorService;
import reggietakeout.service.DishService;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private RedisTemplate redisTemplate;
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

    /**
     * 根据菜品ID获取菜品详细信息，包括菜品的基本信息和口味信息
     *
     * @param id 菜品的唯一标识符
     * @return 返回一个包含菜品详细信息的结果对象
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id) {
        // 根据ID查询菜品基本信息
        Dish dish = dishService.selectById(id);

        // 根据菜品ID查询该菜品的所有口味信息
        List<DishFlavor> flavors = dishFlavorService.selectByDishId(id);

        // 创建一个菜品数据传输对象，用于封装菜品基本信息和口味信息
        DishDto dishDto = new DishDto();
        // 将菜品基本信息复制到菜品数据传输对象中
        BeanUtils.copyProperties(dish, dishDto);
        // 将口味信息设置到菜品数据传输对象中
        dishDto.setFlavors(flavors);

        // 返回包含菜品详细信息的成功结果
        return R.success(dishDto);
    }

    /**
     * 更新菜品信息的接口
     * <p>
     * 该接口接收一个DishDto对象作为参数，用于更新菜品及其相关风味的信息
     * 使用PutMapping注解限定HTTP请求方法为PUT，表示更新操作
     *
     * @param dishDto 要更新的菜品信息，包括菜品的基本信息和风味信息
     * @return 返回一个表示操作结果的响应对象，包含操作是否成功和提示信息
     */
    @PutMapping()
    @Transactional
    public R<String> update(@RequestBody DishDto dishDto) {
        // 记录日志，输出修改菜品的信息，便于调试和审计
        log.info("修改菜品信息：{}", dishDto);

        // 创建一个新的Dish对象，并将DishDto中的属性复制到Dish对象中
        // 这样做是为了将传入的DTO对象转换为实体对象，以便进行数据库操作
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto, dish);

        // 调用dishService的updateById方法，根据ID更新菜品的基本信息
        dishService.updateById(dish);

        // 调用dishFlavorService的updateDishFlavor方法，更新与菜品相关的风味信息
        dishFlavorService.updateDishFlavor(dishDto);

        // 返回成功响应，表示菜品信息修改成功
        return R.success("修改菜品信息成功");
    }

    /**
     * 处理停止销售的请求
     * 该方法接收一个包含多个菜品ID的字符串参数，将这些菜品的状态更新为停止销售
     *
     * @param ids 一个包含多个菜品ID的字符串，ID之间用逗号分隔
     * @return 返回一个表示操作成功的响应对象
     */
    @PostMapping("/status/0")
    public R<String> noSell(@RequestParam("ids") List<Long> ids) {
        // 将输入的字符串按逗号分割，并转换为Long型的ID列表
        ids.stream()
                // 创建Dish对象，设置ID和状态为0（表示停止销售）
                .map(id -> {
                            Dish dish = new Dish();
                            dish.setId(id);
                            dish.setStatus(0);
                            return dish;
                        }
                ).toList()
                // 遍历列表，调用服务层方法更新每个菜品的状态
                .forEach(dishService::updateById);

        // 返回操作成功的响应
        return R.success("停售成功");
    }

    /**
     * 更新菜品状态为起售状态
     * 该接口接收一个或多个菜品ID，将这些菜品的状态更新为起售（状态码为0）
     *
     * @param ids 要更新状态的菜品ID，多个ID用逗号分隔
     * @return 返回一个表示操作结果的响应对象
     */
    @PostMapping("/status/1")
    public R<String> yesSell(@RequestParam("ids") List<Long> ids) {
        // 将传入的ID字符串按逗号分割，并转换为Long类型，创建Dish对象，设置其ID和状态，然后调用更新方法
        ids.stream()
                .map(id -> {
                            // 创建一个新的Dish对象，并设置其ID和状态为0（起售状态）
                            Dish dish = new Dish();
                            dish.setId(id);
                            dish.setStatus(1);
                            return dish;
                        }
                ).toList()
                .forEach(dishService::updateById);

        // 返回成功响应，表示起售操作成功
        return R.success("起售成功");
    }

    /**
     * 删除菜品及其对应口味信息
     * <p>
     * 通过传入的菜品ID列表，首先删除每个菜品对应的口味信息，然后批量删除菜品本身
     * 此方法使用了事务注解，确保操作的原子性，即要么全部删除成功，要么全部不删，避免数据不一致
     *
     * @param ids 要删除的菜品ID列表
     * @return 返回一个表示操作结果的响应对象，包含删除成功的消息
     */
    @DeleteMapping()
    @Transactional
    public R<String> delete(@RequestParam("ids") List<Long> ids) {
        // 遍历菜品ID列表，删除每个菜品对应的口味信息
        ids.forEach(id -> dishFlavorService.deleteByDishId(id));

        // 批量删除菜品
        dishService.removeBatchByIds(ids);

        // 返回删除成功的响应
        return R.success("删除成功");
    }

    /**
     * 根据类别ID获取菜品列表
     *
     * @param categoryId 类别ID，用于筛选菜品
     * @param status     菜品状态，用于筛选菜品
     * @return 返回一个响应对象，包含菜品DTO列表
     */
    @GetMapping("/list")
    public R<List<DishDto>> dishList(@RequestParam("categoryId") Long categoryId, @RequestParam("status") Integer status) {
        String key = "dish_" + categoryId + "_" + status;
        if (redisTemplate.hasKey(key))
            return R.success((List<DishDto>) redisTemplate.opsForValue().get(key));

        // 根据类别ID查询菜品列表
        List<Dish> dishes = dishService.selectByCategoryId(categoryId);

        // 将菜品列表转换为菜品DTO列表，以便返回更丰富的数据结构
        List<DishDto> dishDtos = dishes.stream()
                // 过滤出符合指定状态的菜品
                .filter(dish -> dish.getStatus() == status)
                // 将菜品对象转换为菜品DTO对象
                .map(dish -> {
                    // 创建一个新的菜品DTO对象
                    DishDto dishDto = new DishDto();
                    // 将菜品对象的属性复制到菜品DTO对象中
                    BeanUtils.copyProperties(dish, dishDto);

                    // 根据菜品ID查询对应的口味列表
                    List<DishFlavor> flavors = dishFlavorService.selectByDishId(dish.getId());

                    // 将口味列表设置到菜品DTO对象中
                    dishDto.setFlavors(flavors);

                    // 返回构建好的菜品DTO对象
                    return dishDto;
                }).toList();

        redisTemplate.opsForValue().set(key, dishDtos, 15, TimeUnit.MINUTES);

        // 返回包含菜品DTO列表的成功响应
        return R.success(dishDtos);
    }
}
