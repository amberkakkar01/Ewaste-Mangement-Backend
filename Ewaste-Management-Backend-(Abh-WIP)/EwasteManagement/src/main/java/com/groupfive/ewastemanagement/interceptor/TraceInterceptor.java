package com.groupfive.ewastemanagement.interceptor;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.groupfive.ewastemanagement.helper.Constants.TRACE_ID;

@Component
public class TraceInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        MDC.put(TRACE_ID, request.getHeader(TRACE_ID));
        return true;
    }
}