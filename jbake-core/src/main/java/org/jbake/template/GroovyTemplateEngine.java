package org.jbake.template;


import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import groovy.text.TemplateEngine;
import groovy.text.XmlTemplateEngine;
import org.codehaus.groovy.runtime.MethodClosure;
import org.jbake.db.ContentStore;
import org.jbake.app.configuration.JBakeConfiguration;
import org.jbake.template.model.TemplateModel;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Renders documents using a Groovy template engine. Depending on the file extension of the template, the template
 * engine will either be a {@link groovy.text.SimpleTemplateEngine}, or an {@link groovy.text.XmlTemplateEngine}
 * (.gxml).
 *
 * @author Cédric Champeau
 */
public class GroovyTemplateEngine extends AbstractTemplateEngine {

    private final Map<String, Template> cachedTemplates = new HashMap<>();

    public GroovyTemplateEngine(final JBakeConfiguration config, final ContentStore db) {
        super(config, db);
    }

    @Override
    public void renderDocument(final TemplateModel model, final String templateName, final Writer writer) throws RenderingException {
        try {
            Template template = findTemplate(templateName);
            Writable writable = template.make(wrap(model));
            writable.writeTo(writer);
        } catch (Exception e) {
            throw new RenderingException(e);
        }
    }

    private Template findTemplate(final String templateName) throws SAXException, ParserConfigurationException, ClassNotFoundException, IOException {
        TemplateEngine ste = templateName.endsWith(".gxml") ? new XmlTemplateEngine() : new SimpleTemplateEngine();
        File sourceTemplate = new File(config.getTemplateFolder(), templateName);
        Template template = cachedTemplates.get(templateName);
        if (template == null) {
            template = ste.createTemplate(new InputStreamReader(new BufferedInputStream(new FileInputStream(sourceTemplate)), config.getTemplateEncoding()));
            cachedTemplates.put(templateName, template);
        }
        return template;
    }

    private TemplateModel wrap(final TemplateModel model) {
        return new TemplateModel(model) {
            @Override
            public Object get(Object key) {
                if ("include".equals(key)) {
                    return new MethodClosure(GroovyTemplateEngine.this, "doInclude").curry(this);
                }
                try {
                    return extractors.extractAndTransform(db, (String) key, model, new TemplateEngineAdapter.NoopAdapter());
                } catch (NoModelExtractorException e) {
                    return super.get(key);
                }
            }
        };
    }

    private void doInclude(TemplateModel model, String templateName) throws Exception {
        AbstractTemplateEngine engine = model.getRenderer();
        Writer out = model.getWriter();
        engine.renderDocument(model, templateName, out);
    }
}
