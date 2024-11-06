package reggietakeout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reggietakeout.common.R;
import reggietakeout.dto.SetmealDto;
import reggietakeout.entity.Setmeal;
import reggietakeout.entity.SetmealDish;
import reggietakeout.service.SetmealDishService;
import reggietakeout.service.SetmealService;

import java.util.List;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

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
     * @param page 当前页码
     * @param pageSize 每页记录数
     * @param name 套餐名称关键字
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
}
