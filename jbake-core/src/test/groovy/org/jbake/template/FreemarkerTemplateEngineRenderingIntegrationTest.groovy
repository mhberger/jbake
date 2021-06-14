package org.jbake.template

import org.apache.commons.io.FileUtils
import org.jbake.app.Crawler
import org.jbake.app.Parser
import org.jbake.app.Renderer
import org.jbake.model.DocumentModel
import org.junit.jupiter.api.Test

import java.nio.charset.Charset

import static org.assertj.core.api.Assertions.assertThat
import static org.junit.jupiter.api.Assertions.assertFalse
import static org.junit.jupiter.api.Assertions.assertTrue

class FreemarkerTemplateEngineRenderingIntegrationTest extends org.jbake.template.AbstractTemplateEngineRenderingIntegrationTest {

    FreemarkerTemplateEngineRenderingIntegrationTest() {
        super("freemarkerTemplates", "ftl");
    }

    @Test
    public void renderPaginatedIndex() throws Exception {
        config.setPaginateIndex(true);
        config.setPostsPerPage(1);

        outputStrings.put("index", Arrays.asList(
            "\">Previous</a>",
            "3/\">Next</a>",
            "2 of 3"
        ));

        renderer.renderIndexPaging("index.html");

        File outputFile = new File(destinationFolder, 2 + File.separator + "index.html");
        String output = FileUtils.readFileToString(outputFile, Charset.defaultCharset());

        for (String string : getOutputStrings("index")) {
            assertThat(output).contains(string);
        }

        assertThat(output).contains("Post Url: blog%2F2013%2Fsecond-post.html");
    }

    @Test
    public void shouldFallbackToRenderSingleIndexIfNoPostArePresent() throws Exception {
        config.setPaginateIndex(true);
        config.setPostsPerPage(1);

        contentStoreSqlite.deleteAllByDocType("post");

        renderer.renderIndexPaging("index.html");

        File paginatedFile = new File(destinationFolder, "index2.html");
        assertFalse(paginatedFile.exists(), "paginated file is not rendered");

        File indexFile = new File(destinationFolder, "index.html");
        assertTrue(indexFile.exists(), "index file exists");
    }
}
