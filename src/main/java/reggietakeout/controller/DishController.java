package reggietakeout.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reggietakeout.common.R;
import reggietakeout.dto.DishDto;
import reggietakeout.service.DishFlavorService;
import reggietakeout.service.DishService;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;

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
}
