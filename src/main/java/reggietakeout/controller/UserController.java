package reggietakeout.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reggietakeout.common.R;
import reggietakeout.entity.User;
import reggietakeout.service.UserService;
import reggietakeout.utils.CaptchaUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserService userService;

    /**
     * 发送验证码接口
     * 该方法通过接收HttpServletRequest对象和一个包含用户信息的Map对象，生成并发送验证码
     * 验证码通过日志记录，并存储在当前会话中，与用户的电话号码关联
     *
     * @param request HttpServletRequest对象，用于存储验证码到会话中
     * @param map     包含用户信息（如电话号码）的Map对象
     * @return 返回一个封装了验证码的响应对象
     */
    @PostMapping("/sendMsg")
    public R<String> code(HttpServletRequest request, @RequestBody Map map) {
        // 从请求体中获取用户的电话号码
        String phone = (String) map.get("phone");
        // 生成验证码
        String code = CaptchaUtils.generateCaptcha();
        // 记录日志，方便调试和审计
        log.info("验证码：{}", code);
        // 将验证码与电话号码关联，存储到Redis中，有效期为5分钟
        redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
        // 返回成功响应，携带生成的验证码
        return R.success(code);
    }

    /**
     * 处理用户登录请求
     *
     * @param request HTTP请求对象，用于获取会话属性和设置会话属性
     * @param map     包含用户登录信息的映射，包括电话号码和验证码
     * @return 返回一个封装了登录结果的响应对象
     */
    @PostMapping("/login")
    public R<User> login(HttpServletRequest request, @RequestBody Map map) {
        // 从请求体中获取用户输入的电话号码和验证码
        String phone = (String) map.get("phone");
        String code = (String) map.get("code");

        // 检查Redis中是否存在该电话号码对应的验证码，如果不存在，验证已超时
        if (redisTemplate.opsForValue().get(phone) == null)
            return R.error("验证超时");

        // 比较用户输入的验证码和Redis中存储的验证码是否一致，如果不一致，则返回验证码错误信息
        if (!redisTemplate.opsForValue().get(phone).equals(code))
            return R.error("验证码错误");

        // 调用用户服务，根据电话号码插入或获取用户信息
        User user = userService.insertUser(phone);

        // 检查用户账号状态，如果账号被锁定，则返回错误信息
        if (user.getStatus() == 0)
            return R.error("账号已锁定");

        // 将当前用户信息保存到会话中，以便后续的请求可以识别用户
        request.getSession().setAttribute("user", user.getId());

        // 登录成功后，删除Redis中存储的验证码，避免重复使用
        redisTemplate.delete(phone);

        // 返回成功信息，包含用户信息
        return R.success(user);
    }

    /**
     * 处理用户登出请求的控制器方法
     * <p>
     * 该方法通过HTTP POST请求来处理用户的登出操作它首先从当前请求的会话中移除"user"属性，
     * 以清除用户的相关信息，从而实现用户登出的功能
     *
     * @param request HttpServletRequest对象，用于获取当前请求的会话信息
     * @return 返回一个表示操作结果的R对象，包含一个表示成功登出的消息
     */
    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }
}
