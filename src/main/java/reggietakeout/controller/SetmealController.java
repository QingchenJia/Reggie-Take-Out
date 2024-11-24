package reggietakeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reggietakeout.common.R;
import reggietakeout.dto.SetmealDto;
import reggietakeout.entity.Category;
import reggietakeout.entity.Setmeal;
import reggietakeout.entity.SetmealDish;
import reggietakeout.exception.CustomException;
import reggietakeout.service.CategoryService;
import reggietakeout.service.SetmealDishService;
import reggietakeout.service.SetmealService;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 保存套餐信息
     *
     * @param setmealDto 套餐信息的DTO对象，包含套餐及其关联的菜品信息
     * @return 返回一个表示操作结果的响应对象
     * <p>
     * 此方法首先记录接收到的套餐信息，然后将套餐信息插入到数据库中，
     * 并使用插入后的套餐ID更新DTO对象最后，将与套餐关联的菜品信息也保存到数据库
     * <p>
     * 注意：此方法使用了事务管理，确保在保存套餐及其关联菜品时的数据一致性
     */
    @PostMapping()
    @Transactional
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        // 记录接收到的套餐信息
        log.info("套餐信息：{}", setmealDto);

        // 插入套餐信息到数据库，并获取插入后的套餐ID
        Long setmealId = setmealService.insertSetmeal(setmealDto);
        // 使用插入后的套餐ID更新DTO对象
        setmealDto.setId(setmealId);

        // 保存与套餐关联的菜品信息到数据库
        setmealDishService.insertSetmealDish(setmealDto);

        // 返回表示操作成功的响应对象
        return R.success("新增套餐成功");
    }

    /**
     * 处理套餐分页查询请求
     *
     * @param page     当前页码
     * @param pageSize 每页记录数
     * @param name     套餐名称关键字
     * @return 返回包含套餐信息的分页对象
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page, int pageSize, String name) {
        // 创建Page对象用于存储分页信息
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);

        // 调用service方法，根据页码、每页记录数和套餐名称关键字查询套餐信息
        setmealService.selectPage(pageInfo, name);

        // 获取查询结果中的套餐记录列表
        List<Setmeal> setmeals = pageInfo.getRecords();

        // 将套餐记录转换为套餐DTO列表，便于前端使用
        List<SetmealDto> setmealDtos = setmeals.stream()
                .map(setmeal -> {
                    SetmealDto setmealDto = new SetmealDto();

                    // 复制套餐基本信息到DTO
                    BeanUtils.copyProperties(setmeal, setmealDto);

                    // 根据套餐ID查询套餐菜品信息
                    List<SetmealDish> setmealDishes = setmealDishService.selectBySetmealId(setmeal.getId());
                    setmealDto.setSetmealDishes(setmealDishes);

                    // 查询套餐所属分类信息
                    Category category = categoryService.getById(setmeal.getCategoryId());
                    setmealDto.setCategoryName(category.getName());

                    return setmealDto;
                })
                .toList();

        // 创建Page对象用于存储转换后的套餐DTO分页信息
        Page<SetmealDto> pageResult = new Page<>();

        // 复制分页信息到新的Page对象，但不包括records属性
        BeanUtils.copyProperties(pageInfo, pageResult, "records");

        // 设置转换后的套餐DTO记录到分页对象
        pageResult.setRecords(setmealDtos);

        // 返回包含套餐DTO分页信息的成功响应
        return R.success(pageResult);
    }

    /**
     * 更新套餐状态为停售
     * 该方法通过接收一系列套餐ID，将这些套餐的状态更新为停售（状态码为0）
     *
     * @param ids 待停售的套餐ID列表
     * @return 返回一个表示操作结果的成功消息
     */
    @PostMapping("/status/0")
    public R<String> noSell(@RequestParam("ids") List<Long> ids) {
        // 遍历每个套餐ID，更新其状态为停售
        ids.forEach(id -> {
            Setmeal setmeal = new Setmeal();
            setmeal.setId(id);
            setmeal.setStatus(0);
            setmealService.updateById(setmeal);
        });

        // 返回成功消息，表示停售操作成功
        return R.success("停售成功");
    }

    /**
     * 启售操作的处理方法
     * 该方法通过接收一个菜品ID列表，将这些菜品的状态更新为启售（状态码为1）
     *
     * @param ids 要进行启售操作的菜品ID列表
     * @return 返回一个表示操作结果的成功响应
     */
    @PostMapping("/status/1")
    public R<String> yesSell(@RequestParam("ids") List<Long> ids) {
        // 遍历每个菜品ID，将其状态更新为启售
        ids.forEach(id -> {
            Setmeal setmeal = new Setmeal();
            setmeal.setId(id);
            setmeal.setStatus(1);
            setmealService.updateById(setmeal);
        });

        // 返回成功响应，表示启售操作完成
        return R.success("启售成功");
    }

    /**
     * 处理删除套餐的请求，支持批量删除
     * 该方法使用了DeleteMapping注解来处理HTTP DELETE请求，并使用Transactional注解确保操作的原子性
     *
     * @param ids 要删除的套餐ID列表，通过请求参数传递
     * @return 返回一个表示操作结果的响应对象
     */
    @DeleteMapping()
    @Transactional
    public R<String> delete(@RequestParam("ids") List<Long> ids) {
        // 遍历每个套餐ID
        ids.forEach(id -> {
            // 获取套餐的状态
            Integer status = setmealService.getById(id).getStatus();

            // 检查套餐是否正在售卖，如果是，则抛出异常
            if (status == 1)
                throw new CustomException("套餐正在售卖中，不能删除");

            // 如果套餐不在售卖中，则进行删除操作
            setmealService.removeById(id);
        });

        // 删除与套餐关联的所有菜品信息
        setmealDishService.remove(
                new LambdaQueryWrapper<SetmealDish>()
                        .in(SetmealDish::getSetmealId, ids));

        // 返回删除成功的响应
        return R.success("删除成功");
    }

    /**
     * 根据ID获取套餐详细信息
     *
     * @param id 套餐的ID
     * @return 返回包含套餐详细信息的响应对象
     */
    @GetMapping("{id}")
    public R<SetmealDto> getById(@PathVariable Long id) {
        // 根据ID获取套餐基本信息
        Setmeal setmeal = setmealService.getById(id);

        // 创建一个套餐DTO对象，用于返回套餐详细信息
        SetmealDto setmealDto = new SetmealDto();

        // 将套餐基本信息复制到DTO对象中
        BeanUtils.copyProperties(setmeal, setmealDto);

        // 根据套餐ID查询套餐包含的菜品
        List<SetmealDish> setmealDishes = setmealDishService.selectBySetmealId(id);

        // 将套餐包含的菜品设置到DTO对象中
        setmealDto.setSetmealDishes(setmealDishes);

        // 返回包含套餐详细信息的响应对象
        return R.success(setmealDto);
    }

    /**
     * 更新套餐信息
     * <p>
     * 该方法接收一个套餐信息对象（SetmealDto），并更新数据库中的相应记录
     * 它首先删除现有的套餐菜品关联信息，然后更新套餐基本信息，最后重新插入更新后的套餐菜品关联信息
     *
     * @param setmealDto 套餐信息对象，包含要更新的套餐及其关联的菜品信息
     * @return 返回一个表示操作结果的成功消息
     */
    @PutMapping()
    @Transactional
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        // 记录日志，输出修改的套餐信息
        log.info("修改套餐信息，套餐信息：{}", setmealDto.toString());

        // 删除现有的套餐菜品关联信息，以便后续重新插入更新后的信息
        setmealDishService.remove(
                new LambdaQueryWrapper<SetmealDish>()
                        .eq(SetmealDish::getSetmealId, setmealDto.getId()));

        // 创建一个新的套餐对象，并从传入的套餐信息对象中复制属性
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDto, setmeal);

        // 更新数据库中的套餐信息
        setmealService.updateById(setmeal);

        // 插入更新后的套餐菜品关联信息
        setmealDishService.insertSetmealDish(setmealDto);

        // 返回成功消息
        return R.success("修改成功");
    }

    /**
     * 根据类别ID和状态获取套餐列表
     * 首先尝试从Redis缓存中获取数据，如果缓存不存在，则从数据库中查询，并将结果缓存到Redis中
     *
     * @param categoryId 类别ID，用于筛选套餐类别
     * @param status 套餐状态，通常表示是否可用
     * @return 返回一个包含套餐列表的响应对象
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(@RequestParam("categoryId") Long categoryId, @RequestParam("status") Integer status) {
        // 构造缓存键值
        String key = "setmeal_" + categoryId + "_" + status;
        // 检查缓存是否存在
        if (redisTemplate.hasKey(key))
            // 如果缓存存在，直接返回缓存中的数据
            return R.success((List<Setmeal>) redisTemplate.opsForValue().get(key));

        // 如果缓存不存在，调用服务层方法从数据库中查询数据
        List<Setmeal> setmeals = setmealService.selectByCategoryId(categoryId, status);

        // 将查询到的数据存入缓存，并设置缓存过期时间
        redisTemplate.opsForValue().set(key, setmeals, 15, TimeUnit.MINUTES);

        // 返回查询结果
        return R.success(setmeals);
    }

    /**
     * 根据菜品ID获取菜品详情
     * 此方法使用GET请求来获取特定菜品的详细信息，包括菜品的描述、价格、图片等
     *
     * @param id 菜品的唯一标识符，用于查询特定菜品的信息
     * @return 返回一个SetmealDto对象，包含了菜品的详细信息如果找不到对应的菜品，
     * 则返回null或相应的错误信息
     */
    @GetMapping("/dish/{id}")
    public R<SetmealDto> viewSetmeal(@PathVariable Long id) {
        return getById(id);
    }
}
