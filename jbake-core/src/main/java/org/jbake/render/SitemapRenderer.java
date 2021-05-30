package org.jbake.render;

import org.apache.commons.configuration2.CompositeConfiguration;
import org.jbake.app.ContentStoreOrientDb;
import org.jbake.app.Renderer;
import org.jbake.app.configuration.JBakeConfiguration;
import org.jbake.app.configuration.JBakeConfigurationFactory;
import org.jbake.template.RenderingException;

import java.io.File;


public class SitemapRenderer implements RenderingTool {

    @Override
    public int render(Renderer renderer, ContentStoreOrientDb db, JBakeConfiguration config) throws RenderingException {
        if (config.getRenderSiteMap()) {
            try {
                //TODO: refactor this. the renderer has a reference to the configuration
                renderer.renderSitemap(config.getSiteMapFileName());
                return 1;
            } catch (Exception e) {
                throw new RenderingException(e);
            }
        } else {
            return 0;
        }
    }

    @Override
    public int render(Renderer renderer, ContentStoreOrientDb db, File destination, File templatesPath, CompositeConfiguration config) throws RenderingException {
        JBakeConfiguration configuration = new JBakeConfigurationFactory().createDefaultJbakeConfiguration(templatesPath.getParentFile(), config);
        return render(renderer, db, configuration);
    }

}
