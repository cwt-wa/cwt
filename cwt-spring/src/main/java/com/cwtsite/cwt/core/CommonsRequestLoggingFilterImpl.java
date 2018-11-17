package com.cwtsite.cwt.core;

import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;

public class CommonsRequestLoggingFilterImpl extends CommonsRequestLoggingFilter {

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        if (request == null || "OPTIONS".equals(request.getMethod())) {
            return;
        }

        if (request.getServletPath().startsWith("/api/")) {
            super.afterRequest(request,
                    "method=" + request.getMethod() + ";" + message + ";user-agent=" + request.getHeader("User-Agent"));
        }
    }
}

