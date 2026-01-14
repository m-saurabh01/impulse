package com.wipro.iaf.email.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.wipro.iaf.email.security.SecurityUser;

/**
 * Interceptor to populate session attributes with user info
 */
@Component
public class UserSessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, 
                            HttpServletResponse response, 
                            Object handler) throws Exception {
        
        HttpSession session = request.getSession(false);
        if (session == null) {
            return true;
        }

        // Check if session already has user info
        if (session.getAttribute("userEmail") != null) {
            return true;
        }

        // Get authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityUser) {
            SecurityUser user = (SecurityUser) auth.getPrincipal();
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("userDisplayName", user.getDisplayName());
            session.setAttribute("userId", user.getId());
        }

        return true;
    }
}
