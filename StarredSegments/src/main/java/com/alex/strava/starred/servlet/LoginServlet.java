package com.alex.strava.starred.servlet;

import com.alex.strava.starred.Paths;
import com.alex.strava.starred.listener.ApplicationComponentServletListener;
import com.alex.strava.starred.modules.Templates;
import com.alex.strava.starred.util.ParameterUtil;
import com.alex.strava.starred.util.PropertiesUtil;
import com.alex.strava.starred.util.SessionUtil;
import com.alex.strava.starred.util.UrlUtil;
import javastrava.api.v3.auth.AuthorisationService;
import javastrava.api.v3.service.exception.UnauthorizedException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet({
                    "/",
                    Paths.LOGIN,
})
public class LoginServlet extends HttpServlet {

    @Inject
    @Named("applicationProperties")
    PropertiesUtil applicationProperties;

    @Inject
    AuthorisationService authorisationService;

    @Inject
    @Named("login")
    Templates.StravaTemplate loginTemplate;

    @Override
    public void init() throws ServletException {
        ApplicationComponentServletListener.from(getServletContext()).inject(this);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final ParameterUtil parameters = new ParameterUtil(request);
        final SessionUtil session = new SessionUtil(request.getSession(true));

        final String parametersCode = parameters.code();
        if (parametersCode != null) {
            doTokenExchange(request, response, session, parametersCode);
        } else {
            if (session.token() == null) {
                final String sessionCode = session.code();
                if (sessionCode == null) {
                    doLoginTemplate(request, response);
                } else {
                    doTokenExchange(request, response, session, sessionCode);
                }
            } else {
                doSegmentsRedirect(request, response);
            }
        }
    }

    private void doTokenExchange(final HttpServletRequest request, final HttpServletResponse response, final SessionUtil session, final String code) throws ServletException, IOException {
        try {
            session.token(authorisationService.tokenExchange(applicationProperties.stravaClientId(), applicationProperties.stravaClientSecret(), code));
            response.sendRedirect(request.getContextPath() + Paths.SEGMENTS_LIST);
        } catch (UnauthorizedException e) {
            session.code(null);
            session.token(null);
            response.sendRedirect(request.getContextPath() + Paths.LOGIN);
        }
    }

    private void doLoginTemplate(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final Map<String, Object> data = new HashMap<>();
        data.put("loginUrl", String.format(Paths.STRAVA_AUTH, applicationProperties.stravaClientId(), UrlUtil.buildUrl(request, Paths.LOGIN)));
        loginTemplate.merge(getServletContext(), data, response.getWriter());
    }

    private void doSegmentsRedirect(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + Paths.SEGMENTS_LIST);
    }
}
