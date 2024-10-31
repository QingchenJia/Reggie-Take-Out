package reggietakeout.interceptor;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import reggietakeout.common.BaseContext;
import reggietakeout.common.R;

@Component
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {
    /**
     * 在请求处理之前进行拦截处理
     *
     * @param request 	HttpServletRequest对象，用于获取请求信息
     * @param response 	HttpServletResponse对象，用于设置响应信息
     * @param handler 	请求处理器，可以是处理器方法、拦截器等
     * @return boolean  返回值为true表示继续执行下一个拦截器或处理器方法，返回false表示中断执行
     * @throws Exception 抛出异常表示处理过程中发生错误
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从session中获取用户ID，判断用户是否已登录
        Long userId = (Long) request.getSession().getAttribute("employee");

        // 如果用户ID为空，表示用户未登录
        if (userId == null) {
            // 记录警告日志，提示用户未登录
            log.warn("用户未登录");

            // 创建一个错误响应对象，表示用户未登录
            R<String> error = R.error("NOTLOGIN");
            // 将错误响应对象转换为JSON字符串并写入响应体中
            response.getWriter().write(JSON.toJSONString(error));

            // 中断执行，不再调用下一个拦截器或处理器方法
            return false;
        }

        // 如果用户已登录，将用户ID设置到上下文中，以便后续处理使用
        BaseContext.setCurrentId(userId);
        // 用户已登录，继续执行下一个拦截器或处理器方法
        return true;
    }

}
