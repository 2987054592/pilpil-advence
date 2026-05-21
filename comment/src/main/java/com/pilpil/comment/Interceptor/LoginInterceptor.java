package com.pilpil.comment.Interceptor;


import com.pilpil.comment.entity.UserInfo;
import com.pilpil.comment.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

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
