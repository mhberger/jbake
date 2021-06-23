package org.jbake.app;

import org.jbake.TestUtils;
import org.jbake.app.configuration.ConfigUtil;
import org.jbake.app.configuration.DefaultJBakeConfiguration;
import org.jbake.launcher.Init;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class InitTest {

    @TempDir
    public static Path folder;

    public DefaultJBakeConfiguration config;
    private File rootPath;

    @BeforeEach
    public void setup() throws Exception {

        rootPath = TestUtils.getTestResourcesAsSourceFolder();
        if (!rootPath.exists()) {
            throw new Exception("Cannot find base path for test!");
        }
        config = (DefaultJBakeConfiguration) new ConfigUtil().loadConfig(rootPath);
        // override base template config option
        config.setExampleProject("freemarker", "test.zip");
    }

    @Test
    public void initOK() throws Exception {
        Init init = new Init(config);
        File initPath = folder.resolve("").toFile();
        init.run(initPath, rootPath, "freemarker");
        File testFile = new File(initPath, "testfile.txt");
        assertThat(testFile).exists();
    }

    @Test
    public void initFailDestinationContainsContent() throws IOException {
        Init init = new Init(config);
        File initPath = folder.resolve("init").toFile();
        File contentFolder = new File(initPath.getPath(), config.getContentFolderName());
        contentFolder.mkdir();
        try {
            init.run(initPath, rootPath, "freemarker");
            fail("Shouldn't be able to initialise folder with content folder within it!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        File testFile = new File(initPath, "testfile.txt");
        assertThat(testFile).doesNotExist();
    }

    @Test
    public void initFailInvalidTemplateType() throws IOException {
        Init init = new Init(config);
        File initPath = folder.resolve("init").toFile();
        try {
            init.run(initPath, rootPath, "invalid");
            fail("Shouldn't be able to initialise folder with invalid template type");
        } catch (Exception e) {
            e.printStackTrace();
        }
        File testFile = new File(initPath, "testfile.txt");
        assertThat(testFile).doesNotExist();
    }
}
