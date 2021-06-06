package org.jbake.db

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.jbake.app.DocumentList
import org.jbake.domain.Document
import org.jbake.model.DocumentModel

public class ContentStoreSqlite implements ContentStore {

    Sql db

    ContentStoreSqlite() {
        this.db = new Sql(new org.sqlite.SQLiteDataSource(url: "jdbc:sqlite:mhb_sample_sqlite.db"))
    }

    void createTables() {
        createDocumentsTable()
        createSignaturesTable()
    }

    void createDocumentsTable() {

        db.execute("drop table if exists documents");

        String sql = """
            CREATE TABLE documents (
              id                      INTEGER                     NOT NULL,
              uri                     VARCHAR(255)                NOT NULL,
              name                    VARCHAR(255)                NOT NULL,
              status                  VARCHAR(25)                 NOT NULL,
              type                    VARHCAR(50)                 NOT NULL,
              source_uri              TEXT                        NOT NULL,
              document_date           TEXT                        NOT NULL,
              sha1                    VARHCAR(40)                 NOT NULL,
              rendered                BOOLEAN                     NOT NULL,
              cached                  BOOLEAN                     NOT NULL,
              tags                    TEXT,
              body                    TEXT                        NOT NULL,
              created_timestamp       TIMESTAMP WITH TIME ZONE    NOT NULL DEFAULT (CURRENT_TIMESTAMP),
              last_updated_timestamp  TIMESTAMP WITH TIME ZONE    NOT NULL DEFAULT (CURRENT_TIMESTAMP),
              version                 BIGINT                      NOT NULL DEFAULT (0),

              CONSTRAINT documents_pk PRIMARY KEY ( id )
            );
            """.stripIndent()

        db.execute(sql);
    }

    void createSignaturesTable() {

        db.execute("drop table if exists signatures");

        String sql = """
            CREATE TABLE signatures (
              key                     VARCHAR(25)                 NOT NULL,
              sha1                    VARHCAR(40)                 NOT NULL,
              created_timestamp       TIMESTAMP WITH TIME ZONE    NOT NULL DEFAULT (CURRENT_TIMESTAMP),
              last_updated_timestamp  TIMESTAMP WITH TIME ZONE    NOT NULL DEFAULT (CURRENT_TIMESTAMP),
              version                 BIGINT                      NOT NULL DEFAULT (0),

              CONSTRAINT signatures_pk PRIMARY KEY ( key )
            );
           """.stripIndent()

        db.execute(sql);
    }

    Long addDocumentToDb(Document document) {
        def result = db.executeInsert("""
         insert into documents (
            uri,
            name,
            status,
            type,
            source_uri,
            document_date,
            sha1,
            rendered,
            cached,
            tags,
            body
         )
         values(
            ${document.uri},
            ${document.name},
            ${document.status},
            ${document.type},
            ${document.source_uri},
            ${document.document_date},
            ${document.sha1},
            ${document.rendered},
            ${document.cached},
            ${document.tag_string},
            ${document.body}
         )
         """
        )

        def id = Long.valueOf(result[0][0])
        return id
    }

    // Map from DB row
    Document mapFromDb(GroovyRowResult row) {
        Document document = new Document()
        document.id =            row.id
        document.uri =           row.uri
        document.name =          row.name
        document.status =        row.status
        document.type =          row.type
        document.source_uri =    row.source_uri
        document.document_date = row.document_date
        document.sha1 =          row.sha1
        document.rendered =      row.rendered
        document.cached =        row.cached
        document.tag_string =    row.tags
        document.body =          row.body
        return document
    }

    @Override
    public long getStart() {
        return 0;
    }

    @Override
    public void setStart(int start) {

    }

    @Override
    public long getLimit() {
        return 0;
    }

    @Override
    public void setLimit(int limit) {

    }

    @Override
    public void resetPagination() {

    }

    @Override
    public void updateSchema() {

    }

    @Override
    public long getDocumentCount(String docType) {
        return 0;
    }

    @Override
    public long getPublishedCount(String docType) {
        return 0;
    }

    @Override
    public DocumentList<DocumentModel> getDocumentByUri(String uri) {
        return null;
    }

    @Override
    public DocumentList<DocumentModel> getDocumentStatus(String uri) {
        return null;
    }

    @Override
    public DocumentList<DocumentModel> getPublishedPosts() {
        return null;
    }

    @Override
    public DocumentList<DocumentModel> getPublishedPosts(boolean applyPaging) {
        return null;
    }

    @Override
    public DocumentList<DocumentModel> getPublishedPostsByTag(String tag) {
        return null;
    }

    @Override
    public DocumentList<DocumentModel> getPublishedDocumentsByTag(String tag) {
        return null;
    }

    @Override
    public DocumentList<DocumentModel> getPublishedPages() {
        return null;
    }

    @Override
    public DocumentList<DocumentModel> getPublishedContent(String docType) {
        return null;
    }

    @Override
    public DocumentList<DocumentModel> getAllContent(String docType) {
        return null;
    }

    @Override
    public DocumentList<DocumentModel> getAllContent(String docType, boolean applyPaging) {
        return null;
    }

    @Override
    public DocumentList<DocumentModel> getUnrenderedContent() {
        return null;
    }

    @Override
    public void deleteContent(String uri) {

    }

    @Override
    public void markContentAsRendered(DocumentModel document) {

    }

    @Override
    public void deleteAllByDocType(String docType) {

    }

    @Override
    public Set<String> getTags() {
        return null;
    }

    @Override
    public Set<String> getAllTags() {
        return null;
    }

    @Override
    public void addDocument(DocumentModel document) {

    }

    @Override
    public DocumentList<DocumentModel> getPublishedContent(String docType, boolean applyPaging) {
        return null;
    }

    @Override
    public DocumentList<DocumentModel> getAllTagsFromPublishedPosts() {
        return null;
    }

    @Override
    public DocumentList<DocumentModel> query(String sql) {
        return null;
    }

    @Override
    public DocumentList<DocumentModel> query(String sql, Object... args) {
        return null;
    }

    @Override
    public void executeCommand(String query, Object... args) {

    }

    @Override
    public void deleteAllDocumentTypes() {

    }
}
