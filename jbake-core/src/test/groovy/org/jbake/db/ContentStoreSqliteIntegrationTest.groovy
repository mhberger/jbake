package org.jbake.db

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.jbake.TestUtils
import org.jbake.app.configuration.ConfigUtil
import org.jbake.app.configuration.DefaultJBakeConfiguration
import org.jbake.app.configuration.JBakeConfiguration
import org.jbake.domain.Document
import org.jbake.model.DocumentModel
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.Assert.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

public class ContentStoreSqliteIntegrationTest {

    protected static ContentStoreSqlite contentStoreSqlite;
    protected static DefaultJBakeConfiguration config;
    protected static File sourceFolder;

    @BeforeAll
    public static void setUpClass() throws Exception {

        sourceFolder = TestUtils.getTestResourcesAsSourceFolder();
        assertTrue(sourceFolder.exists(), "Cannot find sample data structure!");

        config = (DefaultJBakeConfiguration) new ConfigUtil().loadConfig(sourceFolder);
        config.setSourceFolder(sourceFolder);

        contentStoreSqlite = new ContentStoreSqlite(config);
        contentStoreSqlite.createTables();
    }

    @AfterAll
    public static void cleanUpClass() {
//        db.close();
    }

    @BeforeEach
    public void setUp() {
        contentStoreSqlite.createTables();
    }

    @AfterEach
    public void tearDown() {
//        db.dropTables();
    }

    private Sql getDb() {
        return contentStoreSqlite.getDb();
    }

    // TODO Review whether this belongs here or not
    static Document makeTestDocument() {
        Document document = new Document(
            name: 'name',
            title: 'title',
            status: 'status',
            type: 'type',
            root_path: 'root_path',
            file: 'file',
            uri: 'test uri 1',
            uri_no_extension: 'uri_no_extensions',
            source_uri: 'source_uri',
            document_date: '2021-06-13',
            sha1: 'sha1',
            rendered: 'rendered',
            cached: 'cached',
            tag_string: 'tag3,tag1',
            body: 'body',
            json_data: "{\"date\":\"2021-01-28T11:00:00+0000\",\"summary\":\"This is a summary\",\"title\":\"documentModel title\",\"type\":\"documentModel type\",\"body\":\"documentModel body\",\"uri\":\"documentModel uri\",\"noExtensionUri\":\"documentModel noExtensionsUri\",\"tags\":[\"tag1\",\"tag2\"],\"sha1\":\"documentModel sha1\",\"file\":\"documentModel file\",\"rendered\":false,\"sourceuri\":\"documentModel sourceuri\",\"cached\":false,\"name\":\"documentModel name\",\"og\":\"{\\\"og:type\\\": \\\"article\\\"}\",\"id\":\"232\",\"rootpath\":\"documentModel rootPath\",\"status\":\"documentModel status\"}"
        )
        document
    }

    // TODO Review whether this belongs here or not
    static DocumentModel makeTestDocumentModel(JBakeConfiguration config = config) {
        DocumentModel documentModel = new DocumentModel()

        documentModel.setName('documentModel name')
        documentModel.setTitle('documentModel title')
        documentModel.setStatus('documentModel status')
        documentModel.setType('documentModel type')
        documentModel.setRootPath('documentModel rootPath')
        documentModel.setFile('documentModel file')
        documentModel.setUri('documentModel uri')
        documentModel.setNoExtensionUri('documentModel noExtensionsUri')
        documentModel.setSourceUri('documentModel sourceuri')
        documentModel.setDate(Date.parse(config.getDateFormat() ,'2020-05-04'))
        documentModel.setSha1('documentModel sha1')
        documentModel.setRendered(false)
        documentModel.setCached(false)
        documentModel.setTags(['tag1', 'tag2'] as String[])
        documentModel.setBody('documentModel body')

        // extra headers
        documentModel["id"]       =  "232"
        documentModel["summary"]  =  "This is a summary"
        documentModel["og"]       =  '{"og:type": "article"}'

        documentModel
    }


    @Test
    public void confirmTablesExist() throws Exception {
        GroovyRowResult result = getDb().firstRow("select count(*) numTables from sqlite_master where type = 'table'");
        assertEquals(3, result.getProperty("numTables"));
    }

    @Test
    public void confirmRowAdded() throws Exception {
        Document document = makeTestDocument()
        contentStoreSqlite.addDocumentToDb(document)

        GroovyRowResult result = getDb().firstRow("select count(*) numTables from documents");
        assertEquals(1, result.getProperty("numTables"));
    }

    @Test
    public void confirmRowReturned() throws Exception {
        Document document = makeTestDocument()
        Long id = contentStoreSqlite.addDocumentToDb(document)
        document.id = id

        GroovyRowResult result = getDb().firstRow("select * from documents where uri = 'test uri 1'");
        Document testDocument = contentStoreSqlite.mapDocumentFromDb(result)

        assertEquals(document, testDocument)
    }

    @Test
    public void confirmGetDocumentCount() throws Exception {
        Document document = makeTestDocument()
        document.setType('post')
        Long id = contentStoreSqlite.addDocumentToDb(document)

        def count = contentStoreSqlite.getDocumentCount('post')

        assertEquals(1, count)
    }

    @Test
    public void confirmDocumentConvertsToDocumentModel() throws Exception {
        Document document = makeTestDocument()
        DocumentModel documentModel = document.toDocumentModel()

        // TODO a bit more
        assertEquals(documentModel.getTitle(), "documentModel title")
    }

    @Test
    public void confirmDocumentConvertsFromDocumentModel() throws Exception {
        DocumentModel documentModel = makeTestDocumentModel()
        Document document = Document.fromDocumentModel(documentModel)

        assertEquals(document.title, documentModel.getTitle())
        assertEquals(document.documentDate(), documentModel.getDate())
    }

    @Test
    public void testSerialisingDocumentModel() throws Exception {
        DocumentModel documentModel = makeTestDocumentModel()
        def json = new JsonBuilder(documentModel).toString()

        def slurper = new JsonSlurper()
        def result = slurper.parseText(json)

        DocumentModel d = new DocumentModel()
        result.each {k, v ->
            if (k == "date") {
                Date documentDate = Document.convertJsonDateToJavaUtilDate(v)
                d.setDate(documentDate)
            }
            else {
                d[k] = v
            }
        }

        assertEquals(documentModel["name"],                 d["name"])
        assertEquals(documentModel["title"],                d["title"])
        assertEquals(documentModel["status"],               d["status"])
        assertEquals(documentModel["type"],                 d["type"])
        assertEquals(documentModel["rootPath"],             d["rootPath"])
        assertEquals(documentModel["file"],                 d["file"])
        assertEquals(documentModel["uri"],                  d["uri"])
        assertEquals(documentModel["noExtensionUri"],       d["noExtensionUri"])
        assertEquals(documentModel["sourceUri"],            d["sourceUri"])
        assertEquals(documentModel["date"],                 d["date"])
        assertEquals(documentModel["sha1"],                 d["sha1"])
        assertEquals(documentModel["rendered"],             d["rendered"])
        assertEquals(documentModel["cached"],               d["cached"])
        assertEquals(documentModel["tags"],                 d["tags"])
        assertEquals(documentModel["body"],                 d["body"])

    }

}
