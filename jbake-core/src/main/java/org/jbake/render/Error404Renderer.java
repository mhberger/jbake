package org.jbake.render;

import org.jbake.db.ContentStore;
import org.jbake.app.Renderer;
import org.jbake.app.configuration.JBakeConfiguration;
import org.jbake.template.RenderingException;


public class Error404Renderer implements RenderingTool {

    @Override
    public int render(Renderer renderer, ContentStore db, JBakeConfiguration config) throws RenderingException {
        if (config.getRenderError404()) {
            try {
                renderer.renderError404(config.getError404FileName());
                return 1;
            } catch (Exception e) {
                throw new RenderingException(e);
            }
        } else {
            return 0;
        }
    }

}
