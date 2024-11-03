package reggietakeout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reggietakeout.common.R;
import reggietakeout.entity.Category;
import reggietakeout.service.CategoryService;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 处理新增分类的请求
     * <p>
     * 该方法接收一个HTTP POST请求，请求体中包含分类信息，用于创建一个新的分类
     *
     * @param category 从请求体中解析出的分类对象，包含要新增分类的信息
     * @return 返回一个表示操作结果的响应对象，包含操作消息
     */
    @PostMapping()
    public R<String> save(@RequestBody Category category) {
        // 记录新增分类的日志，包括分类信息
        log.info("新增分类，分类信息：{}", category);

        // 调用服务层方法插入新的分类信息
        categoryService.insert(category);

        // 返回成功响应，表示新增分类成功
        return R.success("新增分类成功");
    }

    /**
     * 根据页面号和页面大小获取分类信息
     * 此方法响应GET请求，用于分页查询分类信息
     *
     * @param page     页面号，用于指定从哪一页开始查询
     * @param pageSize 页面大小，用于指定每页显示的记录数
     * @return 返回一个封装了分页查询结果的对象
     */
    @GetMapping("/page")
    public R<Page<Category>> page(int page, int pageSize) {
        // 创建一个Page对象，用于封装分页查询的参数
        Page<Category> pageInfo = new Page<>(page, pageSize);

        // 调用服务层方法，执行分页查询
        Page pageResult = categoryService.selectPage(pageInfo);

        // 返回查询结果，封装为R对象表示成功
        return R.success(pageResult);
    }

    /**
     * 处理删除分类信息的请求
     * 通过HTTP DELETE方法接收要删除的分类ID
     *
     * @param id 要删除的分类的ID
     * @return 返回一个响应对象，包含删除操作的结果信息
     */
    @DeleteMapping()
    public R<String> delete(@RequestParam("ids") Long id) {
        // 调用服务层方法，根据id删除分类信息
        categoryService.deleteById(id);

        // 返回成功响应，表示删除成功
        return R.success("删除成功");
    }

    /**
     * 更新分类信息的接口方法
     * 该方法通过HTTP PUT请求接收一个Category对象作为请求体，并尝试更新数据库中的对应分类信息
     *
     * @param category 包含更新后分类信息的Category对象，通过请求体传递
     * @return 返回一个表示操作结果的R对象，包含成功消息
     */
    @PutMapping()
    public R<String> update(@RequestBody Category category) {
        // 记录日志，输出修改分类信息的详细内容
        log.info("修改分类信息：{}", category);

        // 调用服务层方法，根据传入的Category对象更新数据库中的分类信息
        categoryService.updateById(category);

        // 返回成功响应，包含成功消息
        return R.success("修改分类信息成功");
    }
}
