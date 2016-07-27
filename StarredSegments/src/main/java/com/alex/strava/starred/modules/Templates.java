package com.alex.strava.starred.modules;

import dagger.Module;
import dagger.Provides;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import javax.inject.Named;
import javax.servlet.ServletContext;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;

public abstract class Templates {

    public interface StravaTemplate {

        void merge(ServletContext servletContext, Map<String, Object> data, Writer writer);

    }

    @Module
    public static class FreemarkerModule extends Templates {

        @Provides
        Configuration freemargerConfiguration() {
            final Configuration configuration = new Configuration(Configuration.VERSION_2_3_25);
            configuration.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "freemarker/");
            configuration.setEncoding(Locale.US, "UTF-8");
            configuration.setAutoFlush(true);
            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            configuration.setLogTemplateExceptions(false);
            return configuration;
        }

        @Provides
        @Named("login")
        StravaTemplate loginTemplate(final Configuration configuration) {
            try {
                return new FreemarkerStravaTemplate(configuration.getTemplate("login.ftlh"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Provides
        @Named("segmentsList")
        StravaTemplate segmentsListTemplate(final Configuration configuration) {
            try {
                return new FreemarkerStravaTemplate(configuration.getTemplate("segments_list.ftlh"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Provides
        @Named("segmentsSegment")
        StravaTemplate segmentsSegmentTemplate(final Configuration configuration) {
            try {
                return new FreemarkerStravaTemplate(configuration.getTemplate("segments_segment.ftlx"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private static class FreemarkerStravaTemplate implements StravaTemplate {

            private final Template template;

            public FreemarkerStravaTemplate(final Template template) {
                this.template = template;
            }

            @Override
            public void merge(final ServletContext servletContext, final Map<String, Object> data, final Writer writer) {
                try {
                    data.put("contextPath", servletContext.getContextPath());
                    template.process(data, writer);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
