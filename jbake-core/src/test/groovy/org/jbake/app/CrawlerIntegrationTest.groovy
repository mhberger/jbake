package org.jbake.app

import groovy.sql.Sql
import org.apache.commons.io.FilenameUtils
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.jbake.TestUtils
import org.jbake.app.configuration.ConfigUtil
import org.jbake.app.configuration.DefaultJBakeConfiguration
import org.jbake.db.ContentStoreSqlite
import org.jbake.model.DocumentModel
import org.jbake.model.ModelAttributes
import org.jbake.util.DataFileUtil
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.rules.TemporaryFolder

class CrawlerIntegrationTest {
    public static TemporaryFolder folder = new TemporaryFolder();
    protected static ContentStoreSqlite contentStoreSqlite;
    protected static DefaultJBakeConfiguration config;
    protected static File sourceFolder;

    @BeforeAll
    public static void setUpClass() throws Exception {
        sourceFolder = TestUtils.getTestResourcesAsSourceFolder();
        Assertions.assertTrue(sourceFolder.exists(), "Cannot find sample data structure!", );

        config = (DefaultJBakeConfiguration) new ConfigUtil().loadConfig(sourceFolder);
        config.setSourceFolder(sourceFolder);

        Assertions.assertEquals(".html", config.getOutputExtension());

        contentStoreSqlite = new ContentStoreSqlite(config);
        contentStoreSqlite.createTables();
    }

    @AfterAll
    public static void cleanUpClass() {
    }

    @BeforeEach
    public void setUp() {
        contentStoreSqlite.createTables();
    }

    @AfterEach
    public void tearDown() {
    }

    private Sql getDb() {
        return contentStoreSqlite.getDb();
    }

    @Test
    public void crawl() {
        Crawler crawler = new Crawler(contentStoreSqlite, config);
        crawler.crawl();

        Assertions.assertEquals(4, contentStoreSqlite.getDocumentCount("post"));
        Assertions.assertEquals(3, contentStoreSqlite.getDocumentCount("page"));

        DocumentList<DocumentModel> results = contentStoreSqlite.getPublishedPosts();

        Assertions.assertEquals(3, results.size());

        // TODO Work out what this is used for
//        results.each {
//            assertThat(it.getRootPath() == "../../../")
//        }
//        for (Map<String, Object> content : results) {
//            assertThat(content)
//                .containsKey(ModelAttributes.ROOTPATH)
//                .containsValue("../../../");
//        }

        DocumentList<DocumentModel> allPosts = contentStoreSqlite.getAllContent("post");

        Assertions.assertEquals(4, allPosts.size());

        for (DocumentModel content : allPosts) {
            if (content.getTitle().equals("Draft Post")) {
                Assertions.assertTrue(content.containsKey(ModelAttributes.DATE));
            }
        }

        // covers bug #213
        DocumentList<DocumentModel> publishedPostsByTag = contentStoreSqlite.getPublishedPostsByTag("blog");
        Assertions.assertEquals(3, publishedPostsByTag.size());
    }
    @Test
    public void crawlDataFiles() {
        Crawler crawler = new Crawler(contentStoreSqlite, config);

        // manually register data doctype
//        DocumentTypes.addDocumentType(config.getDataFileDocType());
//        db.updateSchema();
        crawler.crawlDataFiles();
        Assertions.assertEquals(1, contentStoreSqlite.getDocumentCount("data"));

        DataFileUtil util = new DataFileUtil(contentStoreSqlite, "data");
        Map<String, Object> data = util.get("videos.yaml");
        Assertions.assertFalse(data.isEmpty());
        Assertions.assertNotNull(data.get("data"));
    }

    @Test
    public void renderWithPrettyUrls() {

        config.setUriWithoutExtension(true);
        config.setPrefixForUriWithoutExtension("/blog");

        Crawler crawler = new Crawler(contentStoreSqlite, config);
        crawler.crawl();

        Assertions.assertEquals(4, contentStoreSqlite.getDocumentCount("post"));
        Assertions.assertEquals(3, contentStoreSqlite.getDocumentCount("page"));

        DocumentList<DocumentModel> documents = contentStoreSqlite.getPublishedPosts();

        for (DocumentModel model : documents) {
            String noExtensionUri = "blog/\\d{4}/" + FilenameUtils.getBaseName(model.getFile()) + "/";

            Assertions.assertTrue(new RegexMatcher(noExtensionUri).matches(model.getNoExtensionUri()))
            Assertions.assertTrue(new RegexMatcher(noExtensionUri + "index\\.html").matches(model.getUri()))
            Assertions.assertEquals("../../../", model.getRootPath());
        }
    }

    private class RegexMatcher extends BaseMatcher<Object> {
        private final String regex;

        public RegexMatcher(String regex) {
            this.regex = regex;
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
