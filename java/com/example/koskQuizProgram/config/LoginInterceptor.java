package com.example.koskQuizProgram.config;

import com.example.koskQuizProgram.model.MemberVO;
import com.example.koskQuizProgram.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Member;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();

        MemberVO memberVO1 = new MemberVO();
        memberVO1.setId("abcd1");
        session.setAttribute("memberVO1", memberVO1);

        MemberVO memberVO = (MemberVO) session.getAttribute("memberVO");
        session.setAttribute("memberVO", memberVO);

        logger.info("Login Interceptor -{}", memberVO);

        if (memberVO1 != null){
            return true;
        }else{
            return false;
        }
    }
}