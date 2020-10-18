package com.cwtsite.cwt.core;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;

public class CommonsRequestLoggingFilterImpl extends CommonsRequestLoggingFilter {

    @Override
    protected void beforeRequest(@NotNull HttpServletRequest request, @NotNull String message) {
    }

    @Override
    protected void afterRequest(@NotNull HttpServletRequest request, @NotNull String message) {
        if ("OPTIONS".equals(request.getMethod())) {
            return;
        }

        if (request.getServletPath().startsWith("/api/")) {
            super.afterRequest(request,
                    "method=" + request.getMethod() + ";" + message + ";user-agent=" + request.getHeader("User-Agent"));
        }
    }
}

