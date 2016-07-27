package com.alex.strava.starred.listener;

import com.alex.strava.starred.modules.ApplicationComponent;
import com.alex.strava.starred.modules.DaggerApplicationComponent;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ApplicationComponentServletListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().setAttribute("applicationComponent", DaggerApplicationComponent.create());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().removeAttribute("applicationComponent");
    }

    public static ApplicationComponent from(ServletContext servletContext) {
        return (ApplicationComponent) servletContext.getAttribute("applicationComponent");
    }
}
