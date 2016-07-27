package com.alex.strava.starred.modules;

import com.alex.strava.starred.modules.scope.Application;
import com.alex.strava.starred.util.PropertiesUtil;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;
import java.io.IOException;

@Module
public class ApplicationModule {

    @Provides
    @Application
    @Named("applicationProperties")
    PropertiesUtil applicationProperties() {
        try {
            return new PropertiesUtil("application.properties");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
