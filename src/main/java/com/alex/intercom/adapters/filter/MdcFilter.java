package com.alex.intercom.adapters.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * Servlet Filter to inject a unique tracking ID into the Mapped Diagnostic Context (MDC)
 * for each incoming intercom request.
 */
@Component
@WebFilter("/*")
public class MdcFilter implements Filter {

    private static final String SESSION_ID_KEY = "callSessionId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            // Generate a unique short tracking ID for the specific interaction
            String uniqueId = UUID.randomUUID().toString().substring(0, 8);
            MDC.put(SESSION_ID_KEY, uniqueId);

            chain.doFilter(request, response);
        } finally {
            // Clean up the MDC context to prevent memory leaks in the thread pool
            MDC.remove(SESSION_ID_KEY);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}