package com.alex.strava.starred.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

public final class UrlUtil {

    private UrlUtil() {
    }

    public static String buildUrl(final HttpServletRequest request, final String path) {
        final String scheme = request.getScheme();
        final int serverPort = request.getServerPort();

        if (("http".equals(scheme) && serverPort == 80)
                    || ("https".equals(scheme) && serverPort == 443)) {
            return String.format("%s://%s%s%s", scheme, request.getServerName(), request.getContextPath(), path);
        } else {
            return String.format("%s://%s:%d%s%s", scheme, request.getServerName(), serverPort, request.getContextPath(), path);
        }
    }

    public static class GoogleStaticMapsBuilder {

        private final StringBuilder builder = new StringBuilder("https://maps.googleapis.com/maps/api/staticmap?");

        private boolean location = false;
        private boolean size = false;

        public GoogleStaticMapsBuilder size(final int width, final int height) {
            return size(width, height, 1);
        }

        public GoogleStaticMapsBuilder center(final MapPoint center, final int zoom) {
            location = true;
            builder.append("&center=").append(center)
                    .append("&zoom=").append(zoom);
            return this;
        }


        public GoogleStaticMapsBuilder size(final int width, final int height, final int scale) {
            size = true;
            builder.append("&size=").append(width / scale).append('x').append(height / scale);
            if (scale != 1) {
                builder.append("&scale=").append(scale);
            }
            return this;
        }

        public GoogleStaticMapsBuilder path(final int weight, final int color, final boolean geodesic, final List<MapPoint> path) {
            location = true;
            // TODO encode path and call other method
            return this;
        }

        public GoogleStaticMapsBuilder path(final int weight, final int color, final boolean geodesic, final String encodedPath) {
            final StringBuilder path = new StringBuilder()
                                               .append("weight:").append(weight)
                                               .append("|color:0x").append(Integer.toHexString((color << 8) | (color >>> 24)));

            if (geodesic) {
                path.append("|geodesic:true");
            }

            path.append("|enc:").append(encodedPath);

            location = true;
            builder.append("&path=").append(encode(path));
            return this;
        }

        public String unsigned(final String key) {
            if (!size) {
                throw new RuntimeException("no size");
            }
            if (!location) {
                throw new RuntimeException("no center, paths or points set");
            }
            return builder.append("&key=").append(encode(key)).toString();
        }

        public String signed(final String key, final String secret) {
            final String unsigned = unsigned(key);

            try {
                final URL url = new URL(unsigned);
                final String resource = url.getPath() + '?' + url.getQuery();

                final Mac mac = Mac.getInstance("HmacSHA1");
                mac.init(new SecretKeySpec(secret.getBytes(), "HmacSHA1"));

                return unsigned + "&signature=" + Base64.getEncoder().encodeToString(mac.doFinal(resource.getBytes()))
                                                          .replace('+', '-')
                                                          .replace('/', '_');
            } catch (InvalidKeyException | NoSuchAlgorithmException | MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        private String encode(final CharSequence s) {
            try {
                return URLEncoder.encode(s.toString(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        public static class MapPoint {

            private final double lat;
            private final double lon;

            public MapPoint(final double lat, final double lon) {
                this.lat = lat;
                this.lon = lon;
            }

            @Override
            public String toString() {
                return String.format(Locale.US, "$f,%f", lat, lon);
            }
        }
    }
}
