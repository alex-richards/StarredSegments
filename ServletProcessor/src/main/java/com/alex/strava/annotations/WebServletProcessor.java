package com.alex.strava.annotations;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class WebServletProcessor extends AbstractProcessor {

    private Set<Element> elements = new HashSet<>();

    private Template webXmlTemplate;

    @Override
    public synchronized void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        try {
            final Configuration configuration = new Configuration(Configuration.VERSION_2_3_25);
            configuration.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "freemarker/");
            configuration.setEncoding(Locale.US, "UTF-8");
            configuration.setAutoFlush(true);
            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            configuration.setLogTemplateExceptions(false);

            webXmlTemplate = configuration.getTemplate("web.ftlx");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        for (final TypeElement annotation : annotations) {
            elements.addAll(roundEnv.getElementsAnnotatedWith(annotation));
        }

        if (roundEnv.processingOver()) {
            try {
                final FileObject webXml = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "", "webapp/WEB-INF/web.xml");

                final Writer webXmlWriter = webXml.openWriter();
                webXmlTemplate.process(Collections.singletonMap("elements", elements), webXmlWriter);
                webXmlWriter.close();

                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Created web.xml");
            } catch (IOException | TemplateException e) {
                throw new RuntimeException(e);
            }
        }

        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        final Set<String> types = new HashSet<>();
        types.add(WebServlet.class.getCanonicalName());
        types.add(WebListener.class.getCanonicalName());
        types.add(WebFilter.class.getCanonicalName());
        // TODO more
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
