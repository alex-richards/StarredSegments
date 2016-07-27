package com.alex.strava.starred.util;

import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {

    private final Properties properties;

    public PropertiesUtil(final String name) throws IOException {
        properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream(name));
    }

    public int stravaClientId() {
        return Integer.parseInt(properties.getProperty("strava.client_id"));
    }

    public String stravaClientSecret() {
        return properties.getProperty("strava.client_secret");
    }

    public String googleStaticMapsKey() {
        return properties.getProperty("google.maps.api_key");
    }

    public String googleStaticMapsSecret() {
        return properties.getProperty("google.maps.url_secret");
    }
}
