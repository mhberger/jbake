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
        System.out.println("MHB Database Implementation Specified: [" + config.getDatabaseImplementation() + "]");
        System.out.println("MHB Database equals OrientDB: [" + (config.getDatabaseImplementation().equals("OrientDB")) + "]");
        if (config.getDatabaseImplementation().equals("OrientDB")) {
            contentStore = DBUtil.createDataStore(config);
            utensils.setContentStore(contentStore);
        } else {
            contentStore = new ContentStoreSqlite();
            contentStore.startup();
        }
        System.out.println("MHB ContentStore class: [" + contentStore.getClass().getName() + "]");
        utensils.setContentStore(contentStore);
        utensils.setCrawler(new Crawler(contentStore, config));
        utensils.setRenderer(new Renderer(contentStore, config));
        utensils.setAsset(new Asset(config));

        return utensils;
    }
}
