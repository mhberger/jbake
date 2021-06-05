package org.jbake.render;

import org.jbake.app.ContentStore;
import org.jbake.app.Renderer;
import org.jbake.app.configuration.JBakeConfiguration;
import org.jbake.template.RenderingException;

public class IndexRenderer implements RenderingTool {

    @Override
    public int render(Renderer renderer, ContentStore db, JBakeConfiguration config) throws RenderingException {
        if (config.getRenderIndex()) {
            try {
                String fileName = config.getIndexFileName();

                //TODO: refactor this. the renderer has a reference to the configuration
                if (config.getPaginateIndex()) {
                    renderer.renderIndexPaging(fileName);
                } else {
                    renderer.renderIndex(fileName);
                }
                return 1;
            } catch (Exception e) {
                throw new RenderingException(e);
            }
        } else {
            return 0;
        }
    }

}
