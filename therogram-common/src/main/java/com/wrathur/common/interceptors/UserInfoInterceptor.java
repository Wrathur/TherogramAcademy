package com.wrathur.common.interceptors;

import cn.hutool.core.util.StrUtil;
import com.wrathur.common.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class UserInfoInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler) throws Exception {
        log.info("成功获取用户登录信息");
        // 获取登录用户信息
        String userInfo = request.getHeader("user-info");
        // 判断是否获取了用户，如果有，存入ThreadLocal
        if (StrUtil.isNotBlank(userInfo)) {
            UserContext.setUser(Integer.valueOf(userInfo));
        }
        // 放行（登录信息校验在过滤器里已完成，不需要在拦截器里实现，不管有没有找到用户信息一律放行）
        return true;
    }

    @Override
    public void afterCompletion(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理用户
        UserContext.removeUser();
    }
}
