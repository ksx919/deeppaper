package com.fanxin.interceptor;

import com.fanxin.util.JwtUtil;
import com.fanxin.util.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

import static com.fanxin.common.ServiceExceptionUtil.exception;
import static com.fanxin.enums.LoginErrorCodeConstants.JWT_FORMAT_ERROR;
import static com.fanxin.enums.LoginErrorCodeConstants.JWT_TOKEN_EXPIRED;

@Component
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 放行 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 令牌验证
        String token = request.getHeader("Authorization");
        try {
            if (!token.startsWith("Bearer ")){
                throw exception(JWT_FORMAT_ERROR);
            }
            token = token.substring(7);
            //从redis中获取相同的token
            String redisToken = stringRedisTemplate.opsForValue().get(token);
            if (redisToken == null) {
                //token失效
                throw exception(JWT_TOKEN_EXPIRED);
            }
            Map<String, Object> claims = JwtUtil.parseToken(token);
            ThreadLocalUtil.set(claims);
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ThreadLocalUtil.remove();
    }
}
