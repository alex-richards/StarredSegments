package com.alex.strava.starred.modules;

import dagger.Module;
import dagger.Provides;
import javastrava.api.v3.auth.AuthorisationService;
import javastrava.api.v3.auth.impl.retrofit.AuthorisationServiceImpl;

@Module
public class StravaAuthenticationModule {

    @Provides
    AuthorisationService authorisationService() {
        return new AuthorisationServiceImpl();
    }

}
