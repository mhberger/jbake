package org.jbake.db

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.jbake.domain.Document
import org.jbake.model.DocumentModel
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import static org.junit.Assert.assertEquals

public class ContentStoreSqliteIntegrationTest {

    protected static ContentStoreSqlite contentStoreSqlite;

    @BeforeClass
    public static void setUpClass() throws Exception {
        contentStoreSqlite = new ContentStoreSqlite();
        contentStoreSqlite.createTables();
    }

    @AfterClass
    public static void cleanUpClass() {
//        db.close();
    }

    @Before
    public void setUp() {
        contentStoreSqlite.createTables();
    }

    @After
    public void tearDown() {
//        db.dropTables();
    }

    private Sql getDb() {
        return contentStoreSqlite.getDb();
    }

    private Document makeTestDocument() {
        Document document = new Document(
            name: 'name',
            title: 'title',
            status: 'status',
            type: 'type',
            root_path: 'root_path',
            file: 'file',
            uri: 'test uri 1',
            uri_no_extensions: 'uri_no_extensions',
            source_uri: 'source_uri',
            document_date: '2021-06-13',
            sha1: 'sha1',
            rendered: 'rendered',
            cached: 'cached',
            tag_string: 'tag3,tag1',
            body: 'body'
        )
        document
    }

    @Test
    public void confirmTablesExist() throws Exception {
        GroovyRowResult result = getDb().firstRow("select count(*) numTables from sqlite_master where type = 'table'");
        assertEquals(2, result.getProperty("numTables"));
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
        Document testDocument = contentStoreSqlite.mapFromDb(result)

        assertEquals(document, testDocument)
    }

    @Test
    public void confirmDocumentConvertsToDocumentModel() throws Exception {
        Document document = makeTestDocument()
        DocumentModel documentModel = document.toDocumentModel()

        assertEquals(documentModel.getTitle(), document.title)
        assertEquals(documentModel.getDate(), document.documentDate())
    }

}
