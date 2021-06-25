package org.jbake;

import org.apache.commons.vfs2.util.Os;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Assume;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectWebsiteTest {

    private static final String WEBSITE_REPO_URL = "https://github.com/jbake-org/jbake.org.git";

    @TempDir
    public static Path folder;
    private File projectFolder;
    private File outputFolder;
    private String jbakeExecutable;
    private BinaryRunner runner;

    @BeforeEach
    public void setup() throws IOException, GitAPIException {
        Assume.assumeTrue("JDK 7 is not supported for this test", !isJava7());
        if (Os.isFamily(Os.OS_FAMILY_WINDOWS)) {
            jbakeExecutable = new File("build\\install\\jbake\\bin\\jbake.bat").getAbsolutePath();
        } else {
            jbakeExecutable = new File("build/install/jbake/bin/jbake").getAbsolutePath();
        }
        projectFolder = folder.resolve("project").toFile();
        new File(projectFolder, "templates");
        outputFolder = new File(projectFolder, "output");

        runner = new BinaryRunner(projectFolder);
        cloneJbakeWebsite();

    }

    private boolean isJava7() {
        return System.getProperty("java.specification.version").equals("1.7");
    }

    private void cloneJbakeWebsite() throws GitAPIException {
        CloneCommand cmd = Git.cloneRepository();
        cmd.setBare(false);
        cmd.setBranch("master");
        cmd.setRemote("origin");
        cmd.setURI(WEBSITE_REPO_URL);
        cmd.setDirectory(projectFolder);

        cmd.call();

        assertThat(new File(projectFolder, "README.md").exists()).isTrue();
    }

    @Test
    public void shouldBakeWebsite() throws IOException, InterruptedException {
        Process process = runner.runWithArguments(jbakeExecutable, "-b");
        assertThat(process.exitValue()).isEqualTo(0);
        assertThat(new File(outputFolder, "index.html")).exists();
        process.destroy();
    }

}
