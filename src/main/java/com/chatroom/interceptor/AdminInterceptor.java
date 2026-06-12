package com.chatroom.interceptor;

import com.chatroom.entity.SessionInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        SessionInfo session = (SessionInfo) request.getAttribute("session");
        if (session == null || !session.isAdmin()) {
            throw new IllegalStateException("admin permission required");
        }
        return true;
    }
}
