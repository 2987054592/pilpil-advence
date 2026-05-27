package com.pilpil.common.Interceptor;


import com.pilpil.common.entity.UserInfo;
import com.pilpil.common.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfo userInfo = UserHolder.get();
        if(userInfo==null){
            response.setStatus(401);
            return false;
        }
        return true;
    }
}
