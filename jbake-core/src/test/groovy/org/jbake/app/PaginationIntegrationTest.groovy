package org.jbake.app


import org.jbake.TestUtils
import org.jbake.app.configuration.ConfigUtil
import org.jbake.app.configuration.DefaultJBakeConfiguration
import org.jbake.db.ContentStoreSqlite
import org.jbake.db.ContentStoreSqliteIntegrationTest
import org.jbake.model.DocumentModel
import org.jbake.model.DocumentTypes
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.assertj.core.api.Assertions.assertThat
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

class PaginationIntegrationTest {

    protected static ContentStoreSqlite contentStoreSqlite;
    protected static DefaultJBakeConfiguration config;
    protected static File sourceFolder;

    @BeforeEach
    public void setUpOwn() {

        sourceFolder = TestUtils.getTestResourcesAsSourceFolder();
        assertTrue(sourceFolder.exists(), "Cannot find sample data structure!");

        config = (DefaultJBakeConfiguration) new ConfigUtil().loadConfig(sourceFolder);
        config.setSourceFolder(sourceFolder);

        assertEquals(".html", config.getOutputExtension());

        contentStoreSqlite = new ContentStoreSqlite(config);
        contentStoreSqlite.createTables();

        for (String docType : DocumentTypes.getDocumentTypes()) {
            String fileBaseName = docType;
            if (docType.equals("masterindex")) {
                fileBaseName = "index";
            }
            config.setTemplateFileNameForDocType(docType, fileBaseName + ".ftl");
        }

        config.setPaginateIndex(true);
        config.setPostsPerPage(1);
    }

    @Test
    public void testPagination() {
        final int TOTAL_POSTS = 5;
        final int PER_PAGE = 2;
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);

        for (int i = 1; i <= TOTAL_POSTS; i++) {
            cal.add(Calendar.SECOND, 5);
            DocumentModel documentModel = ContentStoreSqliteIntegrationTest.makeTestDocumentModel()
            documentModel.setStatus("published")
            documentModel.setDate(cal.getTime())
            contentStoreSqlite.addDocument(documentModel)
        }

        int pageCount = 1;
        int start = 0;

        while (start < TOTAL_POSTS) {
            DocumentList posts = contentStoreSqlite.getPublishedPostsPaged(true, PER_PAGE, start, 'asc');

            assertThat(posts.size()).isLessThanOrEqualTo(2);

            if (posts.size() > 1) {
                DocumentModel post = (DocumentModel) posts.get(0);
                DocumentModel nextPost = (DocumentModel) posts.get(1);

                assertThat(post.getDate()).isAfter(nextPost.getDate());
            }

            pageCount++;
            start += PER_PAGE;
        }
        assertEquals(4, pageCount);
    }
}
