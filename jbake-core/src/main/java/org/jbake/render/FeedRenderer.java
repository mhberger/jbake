package org.jbake.render;

import org.jbake.db.ContentStore;
import org.jbake.app.Renderer;
import org.jbake.app.configuration.JBakeConfiguration;
import org.jbake.template.RenderingException;


public class FeedRenderer implements RenderingTool {

    @Override
    public int render(Renderer renderer, ContentStore db, JBakeConfiguration config) throws RenderingException {
        if (config.getRenderFeed()) {
            try {
                //TODO: refactor this. the renderer has a reference to the configuration
                renderer.renderFeed(config.getFeedFileName());
                return 1;
            } catch (Exception e) {
                throw new RenderingException(e);
            }
        } else {
            return 0;
        }
    }

}
