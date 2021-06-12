package org.jbake.render;

import org.jbake.TestUtils;
import org.jbake.app.Renderer;
import org.jbake.app.configuration.ConfigUtil;
import org.jbake.app.configuration.DefaultJBakeConfiguration;
import org.jbake.db.ContentStoreOrientDb;
import org.jbake.model.DocumentModel;
import org.jbake.template.DelegatingTemplateEngine;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.TemporaryFolder;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

@EnabledIfSystemProperty(named = "jbake.db.implementation", matches = "OrientDB")
@ExtendWith(MockitoExtension.class)
public class RendererTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private DefaultJBakeConfiguration config;
    private File outputPath;

    @Mock
    private ContentStoreOrientDb db;

    @Mock
    private DelegatingTemplateEngine renderingEngine;

    @BeforeEach
    public void setup() throws Exception {

        File sourcePath = TestUtils.getTestResourcesAsSourceFolder();
        if (!sourcePath.exists()) {
            throw new Exception("Cannot find base path for test!");
        }
        outputPath = folder.newFolder("output");
        config = (DefaultJBakeConfiguration) new ConfigUtil().loadConfig(sourcePath);
        config.setDestinationFolder(outputPath);
    }

    /**
     * See issue #300
     *
     * @throws Exception
     */
    @Test
    public void testRenderFileWorksWhenPathHasDotInButFileDoesNot() throws Exception {

        assumeFalse(TestUtils.isWindows(), "Ignore running on Windows");
        String FOLDER = "real.path";

        final String FILENAME = "about";
        config.setOutputExtension("");
        config.setTemplateFolder(folder.newFolder("templates"));
        Renderer renderer = new Renderer(db, config, renderingEngine);

        DocumentModel content = new DocumentModel();
        content.setType("page");
        content.setUri("/" + FOLDER + "/" + FILENAME);
        content.setStatus("published");

        renderer.render(content);

        File outputFile = new File(outputPath.getAbsolutePath() + File.separatorChar + FOLDER + File.separatorChar + FILENAME);
        assertThat(outputFile).isFile();
    }
}
