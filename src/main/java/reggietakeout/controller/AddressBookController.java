package reggietakeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reggietakeout.common.BaseContext;
import reggietakeout.common.R;
import reggietakeout.entity.AddressBook;
import reggietakeout.service.AddressBookService;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 处理地址簿的添加操作
     * 该方法接收一个AddressBook对象作为参数，代表用户的新地址信息
     * 使用@PostMapping注解表明该方法处理POST请求
     *
     * @param addressBook 包含用户地址信息的对象，由请求体中获取
     * @return 返回一个R<String>对象，包含操作结果的响应信息
     */
    @PostMapping()
    public R<String> save(@RequestBody AddressBook addressBook) {
        // 记录新增地址的日志信息
        log.info("新增地址：{}", addressBook);

        // 获取当前用户的ID
        Long userId = BaseContext.getCurrentId();

        // 设置地址簿的用户ID
        addressBook.setUserId(userId);

        // 调用服务层方法保存地址簿信息
        addressBookService.save(addressBook);

        // 返回成功响应，告知客户端地址添加成功
        return R.success("添加成功");
    }

    /**
     * 获取当前用户的地址簿列表
     * <p>
     * 此方法首先获取当前操作用户的ID，然后构造查询条件，查询该用户下的所有地址簿信息
     * 使用LambdaQueryWrapper来构建查询条件，以确保查询结果仅限于当前用户的数据
     *
     * @return 返回一个包装了地址簿列表的响应对象
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list() {
        // 获取当前操作用户的ID
        Long userId = BaseContext.getCurrentId();

        // 构造查询条件，用于查询当前用户的所有地址簿信息
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, userId);

        // 调用服务层方法，查询并返回地址簿列表
        return R.success(addressBookService.list(queryWrapper));
    }

    /**
     * 根据ID获取地址簿信息
     *
     * @param id 地址簿的唯一标识符
     * @return 返回一个响应对象，包含获取的地址簿信息
     */
    @GetMapping("/{id}")
    public R<AddressBook> getById(@PathVariable Long id) {
        // 调用服务层方法，根据ID获取地址簿信息
        AddressBook addressBook = addressBookService.getById(id);
        // 返回成功响应，包含获取的地址簿信息
        return R.success(addressBook);
    }

    /**
     * 更新地址簿中的地址信息
     * <p>
     * 该方法通过接收一个AddressBook对象来更新地址信息它使用PUT HTTP请求来处理更新操作
     * 主要日志记录了更新操作的详细信息，并调用服务层方法进行实际的更新操作
     *
     * @param addressBook 包含更新后地址信息的AddressBook对象，通过请求体传递
     * @return 返回一个表示操作结果的响应对象，包含操作状态和提示信息
     */
    @PutMapping()
    public R<String> update(@RequestBody AddressBook addressBook) {
        // 记录更新地址的信息，便于调试和日志分析
        log.info("修改地址：{}", addressBook);

        // 调用服务层方法，根据ID更新地址信息
        addressBookService.updateById(addressBook);

        // 返回成功响应，表示地址信息已成功修改
        return R.success("修改成功");
    }

    /**
     * 设置默认地址
     * <p>
     * 该方法通过PUT请求更新用户的默认地址信息
     * 使用@Transactional注解确保操作的原子性，即要么全部成功要么全部失败
     *
     * @param map 包含待更新地址信息的映射表，包括地址ID和用户ID
     * @return 返回一个表示操作结果的响应对象，包含操作状态和提示信息
     */
    @PutMapping("/default")
    @Transactional
    public R<String> setDefault(@RequestBody Map map) {
        // 从请求体中获取地址ID和用户ID，并转换为Long类型
        Long id = Long.parseLong(map.get("id").toString());
        Long userId = BaseContext.getCurrentId();

        // 调用服务层方法设置默认地址
        addressBookService.setDefault(id, userId);

        // 返回操作成功的结果响应
        return R.success("修改成功");
    }

    /**
     * 删除地址簿条目
     * <p>
     * 该接口用于通过ID删除地址簿中的特定条目它使用HTTP DELETE方法，并期望提供要删除条目的ID
     *
     * @param id 要删除的地址簿条目的ID
     * @return 返回一个表示删除成功的消息
     */
    @DeleteMapping()
    public R<String> delete(@RequestParam("ids") Long id) {
        addressBookService.removeById(id);

        return R.success("删除成功");
    }

    @GetMapping("/default")
    public R<AddressBook> getDefault() {
        LambdaUpdateWrapper<AddressBook> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId())
                .eq(AddressBook::getIsDefault, 1);

        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        return R.success(addressBook);
    }
}
