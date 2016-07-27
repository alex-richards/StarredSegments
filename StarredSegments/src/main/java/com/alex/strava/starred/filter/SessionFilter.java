package com.alex.strava.starred.filter;

import com.alex.strava.starred.Paths;
import com.alex.strava.starred.util.SessionUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter({
                   Paths.SEGMENTS
})
public class SessionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            final HttpServletRequest httpRequest = (HttpServletRequest) request;
            final SessionUtil session = new SessionUtil(httpRequest.getSession(true));
            if (session.token() == null) {
                final HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.sendRedirect(httpRequest.getContextPath() + Paths.LOGIN);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
