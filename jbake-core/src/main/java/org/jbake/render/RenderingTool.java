package org.jbake.render;

import org.apache.commons.configuration2.CompositeConfiguration;
import org.jbake.app.ContentStoreOrientDb;
import org.jbake.app.ContentStore;
import org.jbake.app.Renderer;
import org.jbake.app.configuration.JBakeConfiguration;
import org.jbake.template.RenderingException;

import java.io.File;

public interface RenderingTool {


    int render(Renderer renderer, ContentStore db, JBakeConfiguration config) throws RenderingException;

}
