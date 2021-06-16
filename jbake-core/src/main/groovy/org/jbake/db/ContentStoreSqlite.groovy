package org.jbake.db

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.jbake.app.DocumentList
import org.jbake.domain.DataFile
import org.jbake.domain.Document
import org.jbake.model.DocumentModel
import org.jbake.model.DocumentTypes
import org.sqlite.SQLiteDataSource

public class ContentStoreSqlite implements ContentStore {

    Sql db

    ContentStoreSqlite() {
        this.db = new Sql(new SQLiteDataSource(url: "jdbc:sqlite:jbake_sample_sqlite.db"))
    }

    void createTables() {
        createDocumentsTable()
        createDataFilesTable()
        createSignaturesTable()
    }

    void createDocumentsTable() {

        db.execute("drop table if exists documents");

        String sql = """
            CREATE TABLE documents (
              id                      INTEGER                     NOT NULL,
              name                    VARCHAR(255),
              title                   TEXT                        NOT NULL,
              status                  VARCHAR(25)                 NOT NULL,
              type                    VARHCAR(50)                 NOT NULL,
              root_path               TEXT,
              file                    TEXT                        NOT NULL,
              uri                     TEXT                        NOT NULL,
              uri_no_extensions       TEXT,
              source_uri              TEXT,
              document_date           TEXT                        NOT NULL,
              sha1                    VARHCAR(40)                 NOT NULL,
              rendered                BOOLEAN                     NOT NULL,
              cached                  BOOLEAN                     NOT NULL,
              tags                    TEXT,
              body                    TEXT                        NOT NULL,
              created_timestamp       TIMESTAMP WITH TIME ZONE    NOT NULL DEFAULT (CURRENT_TIMESTAMP),
              last_updated_timestamp  TIMESTAMP WITH TIME ZONE    NOT NULL DEFAULT (CURRENT_TIMESTAMP),
              version                 BIGINT                      NOT NULL DEFAULT (0),
              json_data               TEXT                        NOT NULL,

              CONSTRAINT documents_pk PRIMARY KEY ( id )
            );
            """.stripIndent()

        db.execute(sql);
    }

    void createDataFilesTable() {

        db.execute("drop table if exists data_files");

        String sql = """
            CREATE TABLE data_files (
              id                      INTEGER                     NOT NULL,
              sha1                    VARHCAR(40)                 NOT NULL,
              rendered                BOOLEAN                     NOT NULL,
              file                    TEXT                        NOT NULL,
              source_uri              TEXT,
              type                    VARHCAR(50)                 NOT NULL,
              data                    TEXT                        NOT NULL,
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

    Long addDataFileToDb(DataFile dataFile) {
        def result = db.executeInsert("""
         insert into data_files (
            sha1,
            rendered,
            file,
            source_uri,
            type,
            data
         )
         values(
            ${dataFile.sha1},
            ${dataFile.rendered},
            ${dataFile.file},
            ${dataFile.source_uri},
            ${dataFile.type},
            ${dataFile.data}
         )
         """
        )

        def id = Long.valueOf(result[0][0])
        return id
    }

    Long addDocumentToDb(Document document) {
        def result = db.executeInsert("""
         insert into documents (
            name,
            title,
            status,
            type,
            root_path,
            file,
            uri,
            uri_no_extensions,
            source_uri,
            document_date,
            sha1,
            rendered,
            cached,
            tags,
            body,
            json_data
         )
         values(
            ${document.name},
            ${document.title},
            ${document.status},
            ${document.type},
            ${document.root_path},
            ${document.file},
            ${document.uri},
            ${document.uri_no_extension},
            ${document.source_uri},
            ${document.document_date},
            ${document.sha1},
            ${document.rendered},
            ${document.cached},
            ${document.tag_string},
            ${document.body},
            ${document.json_data}
         )
         """
        )

        def id = Long.valueOf(result[0][0])
        return id
    }

    // Map from DB row
    static DataFile mapDataFileFromDb(GroovyRowResult row) {
        DataFile dataFile = new DataFile()

        dataFile.id          =  row.id
        dataFile.sha1        =  row.sha1
        dataFile.rendered    =  row.rendered
        dataFile.file        =  row.file
        dataFile.source_uri  =  row.source_uri
        dataFile.type        =  row.type
        dataFile.data        =  row.data

        return dataFile
    }

    // Map from DB row
    static Document mapDocumentFromDb(GroovyRowResult row) {
        Document document = new Document()

        document.id                = row.id
        document.name              = row.name
        document.title             = row.title
        document.status            = row.status
        document.type              = row.type
        document.root_path         = row.root_path
        document.file              = row.file
        document.uri               = row.uri
        document.uri_no_extension  = row.uri_no_extensions
        document.source_uri        = row.source_uri
        document.document_date     = row.document_date
        document.sha1              = row.sha1
        document.rendered          = row.rendered
        document.cached            = row.cached
        document.tag_string        = row.tags
        document.body              = row.body

        document.json_data         = row.json_data

        return document
    }

    @Override
    void startup() {
        createTables()
    }

    // Not needed
    @Override
    public long getStart() {
        return 0;
    }

    // Not needed done per call
    @Override
    public void setStart(int start) {

    }

    // Not needed done per call
    @Override
    public long getLimit() {
        return 0;
    }

    // Not needed done per call
    @Override
    public void setLimit(int limit) {

    }

    // Not needed done per call
    @Override
    public void resetPagination() {

    }

    @Override
    public void updateSchema() {
        createTables()
    }

    // Not needed handled by JDBC
    @Override
    void close() {
        // Will not implement
    }

    // Not needed handled by JDBC
    @Override
    void shutdown() {
        // Will not implement
    }

    // Not needed handled by JDBC
    @Override
    void startupIfEnginesAreMissing() {
        // Will not implement
    }

    // Not needed? Do we need explicit drop?
    @Override
    void drop() {
        // Will not implement
    }

    // Not needed handled by JDBC
    @Override
    void activateOnCurrentThread() {
        // Will not implement
    }

    @Override
    public long getDocumentCount(String docType) {

        GroovyRowResult result

        if (docType == 'data') {
            result = getDb().firstRow("select count(*) count_docs  from data_files");
        }
        else {
            result = getDb().firstRow("select count(*) count_docs  from documents where type = ?", docType);
        }
        result.getProperty('count_docs') as long
    }

    @Override
    public long getPublishedCount(String docType) {
        // select * from Documents where status='published' and type='%s' order by date desc
        GroovyRowResult result = getDb().firstRow("select count(*) count_docs  from documents where status = 'published' and type = ?", docType);
        result.getProperty('count_docs') as long
    }

    @Override
    DocumentList<DocumentModel> getDataFileByUri(String uri) {
        DocumentList<DocumentModel> docs = []
        getDb().rows("select * from data_files where source_uri = ?", uri).each {row ->
            DocumentModel documentModel = mapDataFileFromDb(row).toDocumentModel()
            docs.add(documentModel)
        }
        return docs
    }

    @Override
    public DocumentList<DocumentModel> getDocumentByUri(String uri) {
        GroovyRowResult result = getDb().firstRow("select * from documents where uri = ?", uri);
        return mapDocumentFromDb(result)
    }

    @Override
    public DocumentList<DocumentModel> getDocumentStatus(String uri) {
        DocumentList<DocumentModel> docs = []
        getDb().rows("select * from documents where uri = ?", uri).each {row ->
            DocumentModel documentModel = mapDocumentFromDb(row).toDocumentModel()
            docs.add(documentModel)
        }
        return docs
    }

    @Override
    public DocumentList<DocumentModel> getPublishedPosts() {
        DocumentList<DocumentModel> docs = []
        getDb().rows("select * from Documents where status='published' and type= 'post' order by document_date desc").each {row ->
            DocumentModel documentModel = mapDocumentFromDb(row).toDocumentModel()
            docs.add(documentModel)
        }
        return docs
    }

    @Override
    public DocumentList<DocumentModel> getPublishedPosts(boolean applyPaging) {
        getPublishedContentPaged('post', applyPaging)
    }

    public DocumentList<DocumentModel> getPublishedPostsPaged(boolean applyPaging, Integer start=0, Integer limit=10, String sortOrder="desc") {
        DocumentList<DocumentModel> docs = []
        String sql
        if (applyPaging) {
            sql = "select * from Documents where status = 'published' and type = 'post' order by document_date ${sortOrder} LIMIT ${limit} OFFSET ${start}"
        }
        else {
            sql = "select * from Documents where status = 'published' and type= 'post' order by document_date ${sortOrder}"
        }
        getDb().rows(sql).each { row ->
            DocumentModel documentModel = mapDocumentFromDb(row).toDocumentModel()
            docs.add(documentModel)
        }
        return docs
    }

    @Override
    public DocumentList<DocumentModel> getPublishedPostsByTag(String tag) {
        DocumentList<DocumentModel> docs = []
        String sql = "select * from Documents where type = 'post' and status = 'published' and (tags like '%${tag},%' OR tags like '%,${tag},%')"
        getDb().rows(sql).each {row ->
            DocumentModel documentModel = mapDocumentFromDb(row).toDocumentModel()
            docs.add(documentModel)
        }
        return docs
    }

    @Override
    public DocumentList<DocumentModel> getPublishedDocumentsByTag(String tag) {
        DocumentList<DocumentModel> docs = []
        String sql = "select * from Documents where status = 'published' and (tags like '%${tag},%' OR tags like '%,${tag},%')"
        getDb().rows(sql).each {row ->
            DocumentModel documentModel = mapDocumentFromDb(row).toDocumentModel()
            docs.add(documentModel)
        }
        return docs
    }

    @Override
    public DocumentList<DocumentModel> getPublishedPages() {
        DocumentList<DocumentModel> docs = []
        String sql = "select * from Documents where status = 'published' and type = 'page'"
        getDb().rows(sql).each {row ->
            DocumentModel documentModel = mapDocumentFromDb(row).toDocumentModel()
            docs.add(documentModel)
        }
        return docs
    }

    @Override
    public DocumentList<DocumentModel> getPublishedContent(String docType) {
        DocumentList<DocumentModel> docs = []
        String sql = "select * from Documents where status = 'published' and type = '${docType}'"
        getDb().rows(sql).each {row ->
            DocumentModel documentModel = mapDocumentFromDb(row).toDocumentModel()
            docs.add(documentModel)
        }
        return docs
    }

    @Override
    public DocumentList<DocumentModel> getAllContent(String docType) {
        DocumentList<DocumentModel> docs = []
        getDb().rows("select * from Documents where type= ? order by document_date desc", docType).each {row ->
            DocumentModel documentModel = mapDocumentFromDb(row).toDocumentModel()
            docs.add(documentModel)
        }
        return docs
    }

    @Override
    public DocumentList<DocumentModel> getAllContent(String docType, boolean applyPaging) {
        getAllContentPaged(doctype, applyPaging)
    }

    public DocumentList<DocumentModel> getAllContentPaged(String docType, boolean applyPaging, Integer start=0, Integer limit=10, String sortOrder="desc") {
        DocumentList<DocumentModel> docs = []
        String sql = "select * from Documents where type = '${docType}' order by document_date ${sortOrder}"
        if (applyPaging) {
            sql = sql + " LIMIT ${limit} OFFSET ${start}"
        }
        getDb().rows(sql).each { row ->
            DocumentModel documentModel = mapDocumentFromDb(row).toDocumentModel()
            docs.add(documentModel)
        }
        return docs
    }

    @Override
    public DocumentList<DocumentModel> getUnrenderedContent() {
        DocumentList<DocumentModel> docs = []
        getDb().rows("select * from Documents where rendered=false order by document_date desc").each {row ->
            DocumentModel documentModel = mapDocumentFromDb(row).toDocumentModel()
            docs.add(documentModel)
        }
        return docs
    }

    @Override
    public void deleteContent(String uri) {
        db.execute('delete from documents where uri = ?', uri)
    }

    @Override
    public void markContentAsRendered(DocumentModel document) {
        db.execute('update documents set rendered = true where type = ?', document.getUri())
    }

    @Override
    public void deleteAllByDocType(String docType) {
        db.execute('delete from documents where type = ?', docType)
    }

    // TODO Review. it is actually to get tags from pubilshed posts. See also getAllTagsFromPublishedPosts
    @Override
    public Set<String> getTags() {
        Set<String> tags = []
        String sql = "select tags from Documents where status = 'published' and type = 'post' and tags <> ',' order by document_date desc"
        getDb().rows(sql).each { row ->
            def tagList = row.TAGS.split(/,/)
            tagList.each {
                tags.add(it)
            }
        }
        return tags
    }

    @Override
    public Set<String> getAllTags() {
        Set<String> tags = []
        DocumentTypes.getDocumentTypes().each { doctype ->
            String sql = "select tags from Documents where type= '${doctype}' and tags <> ',' order by document_date desc"
            getDb().rows(sql).each { row ->
                def tagList = row.TAGS.split(/,/)
                tagList.each {
                    tags.add(it)
                }
            }
        }
        return tags
    }

    @Override
    public void addDocument(DocumentModel documentModel) {
        if (documentModel.getType() == 'data') {
            addDataFileToDb(DataFile.fromDocumentModel(documentModel))
        }
        else {
            addDocumentToDb(Document.fromDocumentModel(documentModel))
        }
    }

    @Override
    public DocumentList<DocumentModel> getPublishedContent(String docType, boolean applyPaging) {
        return getPublishedContentPaged(doctype, applyPaging)
    }

    public DocumentList<DocumentModel> getPublishedContentPaged(String docType, boolean applyPaging, Integer start=0, Integer limit=10, String sortOrder="desc") {
        DocumentList<DocumentModel> docs = []
        String sql = "select * from Documents where status = 'published' and type= '${docType}' order by document_date ${sortOrder}"
        if (applyPaging) {
            sql = sql + " LIMIT ${limit} OFFSET ${start}"
        }
        getDb().rows(sql).each { row ->
            DocumentModel documentModel = mapDocumentFromDb(row).toDocumentModel()
            docs.add(documentModel)
        }
        return docs
    }

    // TODO Review. The return type does not reflect method name. See also getTags
    @Override
    public DocumentList<DocumentModel> getAllTagsFromPublishedPosts() {
        Set<String> tags = []
        String sql = "select tags from Documents where status = 'published' and type = 'post' and tags <> ',' order by document_date desc"
        getDb().rows(sql).each { row ->
            def tagList = row.TAGS.split(/,/)
            tagList.each {
                tags.add(it)
            }
        }
        return tags
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

    // Not needed handled by JDBC
    @Override
    void updateAndClearCacheIfNeeded(boolean needed, File templateFolder) {
        // Will not implement
    }

    // Not needed
    @Override
    public void deleteAllDocumentTypes() {

    }
}
