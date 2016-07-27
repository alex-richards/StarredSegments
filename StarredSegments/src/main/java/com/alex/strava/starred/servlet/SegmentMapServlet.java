package com.alex.strava.starred.servlet;

import com.alex.strava.starred.Paths;
import com.alex.strava.starred.listener.ApplicationComponentServletListener;
import com.alex.strava.starred.util.ParameterUtil;
import com.alex.strava.starred.util.PropertiesUtil;
import com.alex.strava.starred.util.SessionUtil;
import com.alex.strava.starred.util.UrlUtil;
import javastrava.api.v3.model.StravaMap;
import javastrava.api.v3.model.StravaSegment;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet({
                    Paths.SEGMENT_MAP
})
public class SegmentMapServlet extends HttpServlet {

    @Inject
    @Named("applicationProperties")
    PropertiesUtil applicationProperties;

    @Override
    public void init() throws ServletException {
        ApplicationComponentServletListener.from(getServletContext()).inject(this);
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final HttpSession httpSession = request.getSession();
        final SessionUtil session = new SessionUtil(httpSession);
        final ParameterUtil parameters = new ParameterUtil(request);

        final StravaSegment segment = session.strava().getSegment(parameters.segmentId());
        final StravaMap map = segment.getMap();

        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", new UrlUtil.GoogleStaticMapsBuilder()
                                               .size(620, 256, 1)
                                               .path(5, 0xAA660000, false, map.getSummaryPolyline() == null
                                                                                   ? map.getPolyline()
                                                                                   : map.getSummaryPolyline())
                                               .unsigned(applicationProperties.googleStaticMapsKey()));
//                                               .signed(applicationProperties.googleStaticMapsKey(), applicationProperties.googleStaticMapsSecret()));
    }
}
