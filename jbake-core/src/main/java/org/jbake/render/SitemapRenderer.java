package org.jbake.render;

import org.jbake.app.ContentStore;
import org.jbake.app.Renderer;
import org.jbake.app.configuration.JBakeConfiguration;
import org.jbake.template.RenderingException;


public class SitemapRenderer implements RenderingTool {

    @Override
    public int render(Renderer renderer, ContentStore db, JBakeConfiguration config) throws RenderingException {
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

}
