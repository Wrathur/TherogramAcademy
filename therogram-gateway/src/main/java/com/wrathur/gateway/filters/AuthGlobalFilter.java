package com.wrathur.gateway.filters;

import cn.hutool.core.text.AntPathMatcher;
import com.wrathur.common.exception.UnauthorizedException;
import com.wrathur.gateway.config.AuthProperties;
import com.wrathur.gateway.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final AuthProperties authProperties;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private final JwtTool jwtTool;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取request
        ServerHttpRequest request = exchange.getRequest();

        System.out.println("Request path: " + exchange.getRequest().getPath());

        // 判断是否需要做登录拦截
        if (isExclude(request.getPath().toString())) {
            System.out.println("Exclude path: " + request.getPath());

            // 放行
            return chain.filter(exchange);
        }
        // 获取token
        String token = null;
        List<String> headers = request.getHeaders().get("authorization");
        if (headers != null && !headers.isEmpty()) {
            token = headers.getFirst();

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
        }

        System.out.println("token: " + token);

        // 校验并解析token
        Long userId;
        try {
            userId = jwtTool.parseToken(token);

            System.out.println("userId: " + userId);

        } catch (UnauthorizedException e) {
            // 拦截，设置响应状态码为401（未授权/未登录）
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        // 传递用户信息
        String userInfo = userId.toString();
        ServerWebExchange swe = exchange.mutate()
                .request(builder -> builder.header("user-info", userInfo))
                .build();
        // 放行
        return chain.filter(swe);
    }

    private boolean isExclude(String path) {
        for (String pathPattern : authProperties.getExcludePaths()) {
            if (antPathMatcher.match(pathPattern, path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
