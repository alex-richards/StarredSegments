package com.alex.strava.starred.servlet;

import com.alex.strava.starred.Paths;
import com.alex.strava.starred.listener.ApplicationComponentServletListener;
import com.alex.strava.starred.modules.Templates;
import com.alex.strava.starred.strava.SegmentExport;
import com.alex.strava.starred.util.ParameterUtil;
import com.alex.strava.starred.util.SessionUtil;
import javastrava.api.v3.model.*;
import javastrava.api.v3.model.reference.StravaStreamResolutionType;
import javastrava.api.v3.model.reference.StravaStreamSeriesDownsamplingType;
import javastrava.api.v3.model.reference.StravaStreamType;
import javastrava.api.v3.service.Strava;
import javastrava.util.Paging;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@WebServlet({
                    Paths.SEGMENTS_LIST,
})
public class SegmentsServlet extends HttpServlet {

    @Inject
    @Named("segmentsList")
    Templates.StravaTemplate segmentsListTemplate;

    @Inject
    @Named("segmentsSegment")
    Templates.StravaTemplate segmentsSegmentTemplate;

    @Override
    public void init() throws ServletException {
        ApplicationComponentServletListener.from(getServletContext()).inject(this);
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final HttpSession httpSession = request.getSession();
        final SessionUtil session = new SessionUtil(httpSession);

        final List<StravaSegment> segments = new Strava(session.token()).listAllStarredSegments(session.token().getAthlete().getId());

        final Map<String, Object> data = new HashMap<>();
        data.put("segments", segments);
        segmentsListTemplate.merge(getServletContext(), data, response.getWriter());
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final HttpSession httpSession = request.getSession();
        final SessionUtil session = new SessionUtil(httpSession);

        final ParameterUtil parameters = new ParameterUtil(request);

        final int[] selected = parameters.selected();
        if (selected == null) {
            doGet(request, response);
            return;
        }

        if (selected.length == 1) {
            final int segmentId = selected[0];
            final SegmentExport segmentExport = getSegmentExport(session.strava(), segmentId, parameters.customName(segmentId));
            response.setContentType("application/vnd.garmin.tcx+xml");
            response.setHeader("Content-Disposition", "attachment; filename=" + segmentExport.getSafeName() + ".crs");
            writeSegment(segmentExport, response.getWriter());

        } else {
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=StarredSegments.zip");
            final ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));
            for (final int segmentId : selected) {
                final SegmentExport segmentExport = getSegmentExport(session.strava(), segmentId, parameters.customName(segmentId));
                zipOutputStream.putNextEntry(new ZipEntry(segmentExport.getSafeName() + ".crs"));
                writeSegment(segmentExport, new OutputStreamWriter(zipOutputStream));
                zipOutputStream.closeEntry();
            }
            zipOutputStream.flush();
            zipOutputStream.close();
        }
    }

    private void writeSegment(final SegmentExport segmentExport, final Writer writer) throws IOException {
        final Map<String, Object> data = new HashMap<>();
        data.put("segment", segmentExport);
        segmentsSegmentTemplate.merge(getServletContext(), data, writer);
    }

    private SegmentExport getSegmentExport(final Strava strava, final int segmentId, final String customName) {
        final StravaSegment segment = strava.getSegment(segmentId);
        final StravaSegmentLeaderboard segmentLeaderboard = strava.getSegmentLeaderboard(segmentId, new Paging(1, 1));
        final StravaSegmentLeaderboardEntry segmentKom = segmentLeaderboard.getEntries().get(0);
        final StravaSegmentEffort komEffort = strava.getSegmentEffort(segmentKom.getEffortId());
        final List<StravaStream> effortStreams = strava.getEffortStreams(komEffort.getId(),
                StravaStreamResolutionType.LOW,
                StravaStreamSeriesDownsamplingType.TIME,
                StravaStreamType.TIME,
                StravaStreamType.MAPPOINT,
                StravaStreamType.DISTANCE,
                StravaStreamType.ALTITUDE);

        List<Float> effortTimes = null;
        List<StravaMapPoint> effortMapPoints = null;
        List<Float> effortDistances = null;
        List<Float> effortAltitudes = null;
        for (final StravaStream effortStream : effortStreams) {
            final StravaStreamType streamType = effortStream.getType();
            if (streamType == StravaStreamType.TIME) {
                effortTimes = effortStream.getData();
            } else if (streamType == StravaStreamType.MAPPOINT) {
                effortMapPoints = effortStream.getMapPoints();
            } else if (streamType == StravaStreamType.DISTANCE) {
                effortDistances = effortStream.getData();
            } else if (streamType == StravaStreamType.ALTITUDE) {
                effortAltitudes = effortStream.getData();
            }
        }

        assert effortTimes != null
                       && effortMapPoints != null
                       && effortDistances != null
                       && effortAltitudes != null;

        assert effortTimes.size() == effortMapPoints.size()
                       && effortMapPoints.size() == effortDistances.size()
                       && effortDistances.size() == effortAltitudes.size();

        final ZonedDateTime startTime = komEffort.getStartDate();

        final List<SegmentExport.Point> points = new LinkedList<>();
        for (int i = 0, s = effortTimes.size(); i < s; ++i) {
            final StravaMapPoint effortMapPoint = effortMapPoints.get(i);
            points.add(new SegmentExport.Point(
                                                      startTime.plusSeconds(effortTimes.get(i).intValue()),
                                                      effortMapPoint.getLatitude(),
                                                      effortMapPoint.getLongitude(),
                                                      effortDistances.get(i),
                                                      effortAltitudes.get(i)));
        }

        return new SegmentExport(
                                        segmentId,
                                        customName == null ? segment.getName() : customName,
                                        segmentKom.getAthleteName(),
                                        komEffort.getElapsedTime(),
                                        segment.getDistance(),
                                        points);
    }
}
