package com.alex.strava.starred.util;

import javastrava.api.v3.auth.model.Token;
import javastrava.api.v3.service.Strava;

import javax.servlet.http.HttpSession;

public class SessionUtil {

    private final HttpSession httpSession;

    public SessionUtil(final HttpSession httpSession) {
        if (httpSession == null) {
            throw new RuntimeException("no session");
        }

        this.httpSession = httpSession;
    }

    public String code(final String code) {
        if (code == null || code.trim().length() == 0) {
            httpSession.setAttribute("code", null);
            return null;
        } else {
            httpSession.setAttribute("code", code);
            return code;
        }
    }

    public String code() {
        return (String) httpSession.getAttribute("code");
    }

    public Token token(final Token token) {
        httpSession.setAttribute("token", token);
        httpSession.setAttribute("strava", new Strava(token));
        return token;
    }

    public Token token() {
        return (Token) httpSession.getAttribute("token");
    }

    public Strava strava() {
        return (Strava) httpSession.getAttribute("strava");
    }
}
