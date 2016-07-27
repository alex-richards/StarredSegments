package com.alex.strava.starred.modules;

import com.alex.strava.starred.modules.scope.Application;
import com.alex.strava.starred.servlet.LoginServlet;
import com.alex.strava.starred.servlet.SegmentMapServlet;
import com.alex.strava.starred.servlet.SegmentsServlet;
import dagger.Component;

@Application
@Component(modules = {
                             ApplicationModule.class,
                             StravaAuthenticationModule.class,
                             Templates.FreemarkerModule.class,
})
public interface ApplicationComponent {

    void inject(LoginServlet loginServlet);

    void inject(SegmentsServlet segmentsServlet);

    void inject(SegmentMapServlet segmentMapServlet);

}
