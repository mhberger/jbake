package org.jbake.app

import org.jbake.TestUtils
import org.jbake.app.configuration.ConfigUtil
import org.jbake.app.configuration.DefaultJBakeConfiguration
import org.jbake.db.ContentStoreSqlite
import org.jbake.model.DocumentTypes
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import java.nio.file.Path

import static org.assertj.core.api.Assertions.assertThat

class OvenIntegrationTest {

    @TempDir
    public Path root;

    private DefaultJBakeConfiguration configuration;
    private File sourceFolder;
//    private ContentStoreSqlite contentStore;

    @BeforeEach
    public void setUp() throws Exception {
//        contentStore = new ContentStoreSqlite();
//        contentStore.createTables();

        // reset values to known state otherwise previous test case runs can affect the success of this test case
        DocumentTypes.resetDocumentTypes();
        File output = root.resolve("output").toFile();
        sourceFolder = TestUtils.getTestResourcesAsSourceFolder();
        configuration = (DefaultJBakeConfiguration) new ConfigUtil().loadConfig(sourceFolder);
        configuration.setDestinationFolder(output);
        configuration.setTemplateFolder(new File(sourceFolder, "groovyMarkupTemplates"));
        configuration.setProperty("template.paper.file", "paper.tpl");
    }

    @AfterEach
    public void tearDown() {
//        if (contentStore != null && contentStore.isActive()) {
//            contentStore.close();
//            contentStore.shutdown();
//        }
    }

    @Test
    public void bakeWithAbsolutePaths() {
        configuration.setTemplateFolder(new File(sourceFolder, "groovyMarkupTemplates"));
        configuration.setContentFolder(new File(sourceFolder, "content"));
        configuration.setAssetFolder(new File(sourceFolder, "assets"));

        final Oven oven = new Oven(configuration);
        oven.bake();

        assertThat(oven.getErrors()).isEmpty();
    }


}
