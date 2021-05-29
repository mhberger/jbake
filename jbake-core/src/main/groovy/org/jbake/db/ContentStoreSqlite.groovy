package org.jbake.db

import org.jbake.app.DocumentList;
import org.jbake.model.DocumentModel;

import org.sqlite.SQLiteDataSource
import groovy.sql.Sql

import java.util.Set;

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
              id                      BIGINT                      NOT NULL,
              uri                     VARCHAR(255)                NOT NULL,
              name                    VARCHAR(255)                NOT NULL,
              status                  VARCHAR(25)                 NOT NULL,
              type                    VARHCAR(50)                 NOT NULL,
              source_uri              TEXT                        NOT NULL,
              document_date           TIMESTAMP WITH TIME ZONE    NOT NULL,
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
