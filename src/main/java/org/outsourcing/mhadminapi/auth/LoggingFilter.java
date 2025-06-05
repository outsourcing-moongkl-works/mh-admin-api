package org.outsourcing.mhadminapi.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        long start = System.currentTimeMillis();
        ContentCachingRequestWrapper wrappedReq = new ContentCachingRequestWrapper(req);
        ContentCachingResponseWrapper wrappedRes = new ContentCachingResponseWrapper(res);

        chain.doFilter(wrappedReq, wrappedRes);

        long time = System.currentTimeMillis() - start;
        String body = new String(wrappedReq.getContentAsByteArray(), StandardCharsets.UTF_8);
        String resp = new String(wrappedRes.getContentAsByteArray(), StandardCharsets.UTF_8);

        log.info("[{}] {} {}ms\nREQ {}\nRES {}", res.getStatus(),
                req.getMethod() + " " + req.getRequestURI(), time, body, resp);

        wrappedRes.copyBodyToResponse();   // 중요!
    }
}

