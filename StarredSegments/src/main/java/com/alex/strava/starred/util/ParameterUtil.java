package com.alex.strava.starred.util;

import javax.servlet.http.HttpServletRequest;

public final class ParameterUtil {

    private final HttpServletRequest servletRequest;

    public ParameterUtil(final HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    public String code() {
        final String code = servletRequest.getParameter("code");
        if (code != null && code.trim().length() > 0) {
            return code;
        } else {
            return null;
        }
    }

    public int[] selected() {
        final String[] values = servletRequest.getParameterValues("selected");
        if (values != null && values.length > 0) {
            final int[] selected = new int[values.length];
            for (int i = 0; i < values.length; ++i) {
                selected[i] = Integer.parseInt(values[i]);
            }
            return selected;
        } else {
            return null;
        }
    }

    public String customName(final int segmentId) {
        final String customName = servletRequest.getParameter("name_" + segmentId);
        if (customName != null && customName.trim().length() > 0) {
            return customName;
        } else {
            return null;
        }
    }

    public int segmentId() {
        final String value = servletRequest.getParameter("segmentId");
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
