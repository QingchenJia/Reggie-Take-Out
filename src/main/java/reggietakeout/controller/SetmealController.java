package reggietakeout.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reggietakeout.common.R;
import reggietakeout.dto.SetmealDto;
import reggietakeout.service.SetmealDishService;
import reggietakeout.service.SetmealService;

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
     *
     * 此方法首先记录接收到的套餐信息，然后将套餐信息插入到数据库中，
     * 并使用插入后的套餐ID更新DTO对象最后，将与套餐关联的菜品信息也保存到数据库
     *
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
}
