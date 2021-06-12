package org.jbake.app;

import org.apache.commons.io.FilenameUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jbake.db.ContentStoreOrientDbIntegrationTest;
import org.jbake.model.DocumentModel;
import org.jbake.model.DocumentTypes;
import org.jbake.model.ModelAttributes;
import org.jbake.util.DataFileUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;

@EnabledIfSystemProperty(named = "jbake.db.implementation", matches = "OrientDB*")
public class CrawlerTest extends ContentStoreOrientDbIntegrationTest {

    @Test
    public void crawl() {
        Crawler crawler = new Crawler(db, config);
        crawler.crawl();

        assertEquals(4, db.getDocumentCount("post"));
        assertEquals(3, db.getDocumentCount("page"));

        DocumentList<DocumentModel> results = db.getPublishedPosts();

        assertThat(results.size()).isEqualTo(3);

        for (Map<String, Object> content : results) {
            assertThat(content)
                    .containsKey(ModelAttributes.ROOTPATH)
                    .containsValue("../../../");
        }

        DocumentList<DocumentModel> allPosts = db.getAllContent("post");

        assertThat(allPosts.size()).isEqualTo(4);

        for (DocumentModel content : allPosts) {
            if (content.getTitle().equals("Draft Post")) {
                assertThat(content).containsKey(ModelAttributes.DATE);
            }
        }

        // covers bug #213
        DocumentList<DocumentModel> publishedPostsByTag = db.getPublishedPostsByTag("blog");
        assertEquals(3, publishedPostsByTag.size());
    }

    @Test
    public void crawlDataFiles() {
        Crawler crawler = new Crawler(db, config);
        // manually register data doctype
        DocumentTypes.addDocumentType(config.getDataFileDocType());
        db.updateSchema();
        crawler.crawlDataFiles();
        assertEquals(1, db.getDocumentCount("data"));

        DataFileUtil util = new DataFileUtil(db, "data");
        Map<String, Object> data = util.get("videos.yaml");
        assertFalse(data.isEmpty());
        assertNotNull(data.get("data"));
    }

    @Test
    public void renderWithPrettyUrls() {

        config.setUriWithoutExtension(true);
        config.setPrefixForUriWithoutExtension("/blog");

        Crawler crawler = new Crawler(db, config);
        crawler.crawl();

        assertEquals(4, db.getDocumentCount("post"));
        assertEquals(3, db.getDocumentCount("page"));

        DocumentList<DocumentModel> documents = db.getPublishedPosts();

        for (DocumentModel model : documents) {
            String noExtensionUri = "blog/\\d{4}/" + FilenameUtils.getBaseName(model.getFile()) + "/";

            assertEquals(model.getNoExtensionUri(), RegexMatcher.matches(noExtensionUri));
            assertEquals(model.getUri(), RegexMatcher.matches(noExtensionUri + "index\\.html"));
            assertEquals(model.getRootPath(), is("../../../"));
        }
    }

    private static class RegexMatcher extends BaseMatcher<Object> {
        private final String regex;

        public RegexMatcher(String regex) {
            this.regex = regex;
        }

        public static RegexMatcher matches(String regex) {
            return new RegexMatcher(regex);
        }

        @Override
        public boolean matches(Object o) {
            return ((String) o).matches(regex);

        }

        @Override
        public void describeTo(Description description) {
            description.appendText("matches regex: " + regex);
        }
    }
}
