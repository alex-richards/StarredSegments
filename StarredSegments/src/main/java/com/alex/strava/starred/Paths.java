package com.alex.strava.starred;

import java.util.regex.Pattern;

public final class Paths {

    private Paths() {
    }

    public static final String LOGIN = "/login.do";

    public static final String SEGMENTS = "/segments/*";
    public static final String SEGMENTS_LIST = "/segments/list.do";
    public static final String SEGMENT_MAP = "/segments/map.do";

    public static final String STRAVA_AUTH = "https://www.strava.com/oauth/authorize" +
                                                     "?client_id=%d" +
                                                     "&response_type=code" +
                                                     "&redirect_uri=%s" +
                                                     // "&scope=write" +
                                                     // "&state=mystate" +
                                                     "&approval_prompt=auto";

}
