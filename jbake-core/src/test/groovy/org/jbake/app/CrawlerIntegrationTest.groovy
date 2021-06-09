package org.jbake.app

import groovy.sql.Sql
import org.jbake.TestUtils
import org.jbake.app.configuration.ConfigUtil
import org.jbake.app.configuration.DefaultJBakeConfiguration
import org.jbake.db.ContentStoreSqlite
import org.jbake.model.DocumentModel
import org.jbake.model.DocumentTypes
import org.jbake.model.ModelAttributes
import org.jbake.util.DataFileUtil
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.assertThat

//@Ignore("Need to fix up the DocumentList first.")
class CrawlerIntegrationTest {
    public static TemporaryFolder folder = new TemporaryFolder();
    protected static ContentStoreSqlite contentStoreSqlite;
    protected static DefaultJBakeConfiguration config;
    protected static File sourceFolder;

    @BeforeClass
    public static void setUpClass() throws Exception {
        contentStoreSqlite = new ContentStoreSqlite();
        contentStoreSqlite.createTables();

        sourceFolder = TestUtils.getTestResourcesAsSourceFolder();
        Assert.assertTrue("Cannot find sample data structure!", sourceFolder.exists());

        config = (DefaultJBakeConfiguration) new ConfigUtil().loadConfig(sourceFolder);
        config.setSourceFolder(sourceFolder);

        Assert.assertEquals(".html", config.getOutputExtension());

    }

    @AfterClass
    public static void cleanUpClass() {
    }

    @Before
    public void setUp() {
        contentStoreSqlite.createTables();
    }

    @After
    public void tearDown() {
    }

    private Sql getDb() {
        return contentStoreSqlite.getDb();
    }

    @Test
    public void crawl() {
        Crawler crawler = new Crawler(contentStoreSqlite, config);
        crawler.crawl();

        Assert.assertEquals(4, contentStoreSqlite.getDocumentCount("post"));
        Assert.assertEquals(3, contentStoreSqlite.getDocumentCount("page"));

        DocumentList<DocumentModel> results = contentStoreSqlite.getPublishedPosts();

        assertThat(results.size()).isEqualTo(3);

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

        assertThat(allPosts.size()).isEqualTo(4);

        for (DocumentModel content : allPosts) {
            if (content.getTitle().equals("Draft Post")) {
                assertThat(content).containsKey(ModelAttributes.DATE);
            }
        }

        // covers bug #213
        DocumentList<DocumentModel> publishedPostsByTag = contentStoreSqlite.getPublishedPostsByTag("blog");
        Assert.assertEquals(3, publishedPostsByTag.size());
    }
    @Test
    public void crawlDataFiles() {
        Crawler crawler = new Crawler(contentStoreSqlite, config);

        // manually register data doctype
//        DocumentTypes.addDocumentType(config.getDataFileDocType());
//        db.updateSchema();
        crawler.crawlDataFiles();
        Assert.assertEquals(1, contentStoreSqlite.getDocumentCount("data"));

        DataFileUtil util = new DataFileUtil(contentStoreSqlite, "data");
        Map<String, Object> data = util.get("videos.yaml");
        Assert.assertFalse(data.isEmpty());
        Assert.assertNotNull(data.get("data"));
    }


}
