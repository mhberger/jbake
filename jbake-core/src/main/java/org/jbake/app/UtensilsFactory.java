package org.jbake.app;

import org.jbake.app.configuration.JBakeConfiguration;
import org.jbake.app.configuration.JBakeConfigurationInspector;
import org.jbake.db.ContentStore;
import org.jbake.db.ContentStoreSqlite;

/**
 * A factory to create a {@link Utensils} object
 */
public class UtensilsFactory {

    /**
     * Create default {@link Utensils} by a given {@link JBakeConfiguration}
     * @param config a {@link JBakeConfiguration}
     * @return a default {@link Utensils} instance
     */
    public static Utensils createDefaultUtensils(JBakeConfiguration config) {

        JBakeConfigurationInspector inspector = new JBakeConfigurationInspector(config);
        inspector.inspect();

        Utensils utensils = new Utensils();
        utensils.setConfiguration(config);
        ContentStore contentStore;
        if (config.getDatabaseImplementation() == "OrientDB") {
            contentStore = DBUtil.createDataStore(config);
            utensils.setContentStore(contentStore);
        } else {
            contentStore = new ContentStoreSqlite();
            contentStore.startup();
        }
        utensils.setContentStore(contentStore);
        utensils.setCrawler(new Crawler(contentStore, config));
        utensils.setRenderer(new Renderer(contentStore, config));
        utensils.setAsset(new Asset(config));

        return utensils;
    }
}
