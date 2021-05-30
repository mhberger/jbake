/*
 * The MIT License
 *
 * Copyright 2015 jdlee.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jbake.app;

import com.orientechnologies.common.log.OLogManager;
import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.ODatabaseType;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import org.jbake.model.DocumentModel;
import org.jbake.model.DocumentTypes;
import org.jbake.model.ModelAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author jdlee
 */
public class ContentStoreOrientDb extends AbstractContentStore {

    private final Logger logger = LoggerFactory.getLogger(ContentStoreOrientDb.class);
    private final String type;
    private final String name;

    private ODatabaseSession db;

    private long start = -1;
    private long limit = -1;
    private OrientDB orient;

    public ContentStoreOrientDb(final String type, String name) {
        this.type = type;
        this.name = name;
    }


    public void startup() {
        startupIfEnginesAreMissing();

        if (type.equalsIgnoreCase(ODatabaseType.PLOCAL.name())) {
            orient = new OrientDB(type + ":" + name, OrientDBConfig.defaultConfig());
        } else {
            orient = new OrientDB(type + ":", OrientDBConfig.defaultConfig());
        }

        orient.createIfNotExists(name, ODatabaseType.valueOf(type.toUpperCase()));

        db = orient.open(name, "admin", "admin");

        activateOnCurrentThread();

        updateSchema();
    }

    public long getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void resetPagination() {
        this.start = -1;
        this.limit = -1;
    }

    @Override
    public final void updateSchema() {

        OSchema schema = db.getMetadata().getSchema();

        if (!schema.existsClass(Schema.DOCUMENTS)) {
            createDocType(schema);
        }
        if (!schema.existsClass(Schema.SIGNATURES)) {
            createSignatureType(schema);
        }
    }

    public void close() {
        if (db != null) {
            activateOnCurrentThread();
            db.close();
        }

        if (orient != null) {
            orient.close();
        }
        DBUtil.closeDataStore();
    }

    public void shutdown() {

//        Orient.instance().shutdown();
    }

    private void startupIfEnginesAreMissing() {
        // Using a jdk which doesn't bundle a javascript engine
        // throws a NoClassDefFoundError while logging the warning
        // see https://github.com/orientechnologies/orientdb/issues/5855
        OLogManager.instance().setWarnEnabled(false);

        // If an instance of Orient was previously shutdown all engines are removed.
        // We need to startup Orient again.
        if (Orient.instance().getEngines().isEmpty()) {
            Orient.instance().startup();
        }
        OLogManager.instance().setWarnEnabled(true);
    }

    public void drop() {
        activateOnCurrentThread();
//        db.drop();

        orient.drop(name);
    }

    private void activateOnCurrentThread() {
        if (db != null) {
            db.activateOnCurrentThread();
        } else {
            System.out.println("db is null on activate");
        }
    }

    @Override
    public long getDocumentCount(String docType) {
        activateOnCurrentThread();
        String statement = String.format(ContentStore.STATEMENT_GET_DOCUMENT_COUNT_BY_TYPE, docType);
        return (long) query(statement).get(0).get("count");
    }

    @Override
    public long getPublishedCount(String docType) {
        String statement = String.format(ContentStore.STATEMENT_GET_PUBLISHED_COUNT, docType);
        return (long) query(statement).get(0).get("count");
    }

    @Override
    public DocumentList<DocumentModel> getDocumentByUri(String uri) {
        return query("select * from Documents where sourceuri=?", uri);
    }

    @Override
    public DocumentList<DocumentModel> getDocumentStatus(String uri) {
        return query(ContentStore.STATEMENT_GET_DOCUMENT_STATUS_BY_DOCTYPE_AND_URI, uri);
    }

    @Override
    public DocumentList<DocumentModel> getPublishedPosts() {
        return getPublishedContent("post");
    }

    public DocumentList<DocumentModel> getPublishedPosts(boolean applyPaging) {
        return getPublishedContent("post", applyPaging);
    }

    @Override
    public DocumentList<DocumentModel> getPublishedPostsByTag(String tag) {
        return query(ContentStore.STATEMENT_GET_PUBLISHED_POSTS_BY_TAG, tag);
    }

    @Override
    public DocumentList<DocumentModel> getPublishedDocumentsByTag(String tag) {
        final DocumentList<DocumentModel> documents = new DocumentList<>();

        for (final String docType : DocumentTypes.getDocumentTypes()) {
            String statement = String.format(ContentStore.STATEMENT_GET_PUBLISHED_POST_BY_TYPE_AND_TAG, docType);
            DocumentList<DocumentModel> documentsByTag = query(statement, tag);
            documents.addAll(documentsByTag);
        }
        return documents;
    }

    @Override
    public DocumentList<DocumentModel> getPublishedPages() {
        return getPublishedContent("page");
    }

    @Override
    public DocumentList<DocumentModel> getPublishedContent(String docType) {
        return getPublishedContent(docType, false);
    }

    @Override
    public DocumentList<DocumentModel> getPublishedContent(String docType, boolean applyPaging) {
        String query = String.format(ContentStore.STATEMENT_GET_PUBLISHED_CONTENT_BY_DOCTYPE, docType);
        if (applyPaging && hasStartAndLimitBoundary()) {
            query += " SKIP " + start + " LIMIT " + limit;
        }
        return query(query);
    }

    @Override
    public DocumentList<DocumentModel> getAllContent(String docType) {
        return getAllContent(docType, false);
    }

    @Override
    public DocumentList<DocumentModel> getAllContent(String docType, boolean applyPaging) {
        String query = String.format(ContentStore.STATEMENT_GET_ALL_CONTENT_BY_DOCTYPE, docType);
        if (applyPaging && hasStartAndLimitBoundary()) {
            query += " SKIP " + start + " LIMIT " + limit;
        }
        return query(query);
    }

    private boolean hasStartAndLimitBoundary() {
        return (start >= 0) && (limit > -1);
    }

    @Override
    public DocumentList<DocumentModel> getAllTagsFromPublishedPosts() {
        return query(ContentStore.STATEMENT_GET_TAGS_FROM_PUBLISHED_POSTS);
    }

    private DocumentList<DocumentModel> getSignaturesForTemplates() {
        return query(ContentStore.STATEMENT_GET_SIGNATURE_FOR_TEMPLATES);
    }

    @Override
    public DocumentList<DocumentModel> getUnrenderedContent() {
        return query(ContentStore.STATEMENT_GET_UNDRENDERED_CONTENT);
    }

    @Override
    public void deleteContent(String uri) {
        executeCommand(ContentStore.STATEMENT_DELETE_DOCTYPE_BY_SOURCEURI, uri);
    }

    @Override
    public void markContentAsRendered(DocumentModel document) {
        String statement = String.format(ContentStore.STATEMENT_MARK_CONTENT_AS_RENDERD, document.getType(), document.getSourceuri());
        executeCommand(statement);
    }

    private void updateSignatures(String currentTemplatesSignature) {
        executeCommand(ContentStore.STATEMENT_UPDATE_TEMPLATE_SIGNATURE, currentTemplatesSignature);
    }

    @Override
    public void deleteAllByDocType(String docType) {
        String statement = String.format(ContentStore.STATEMENT_DELETE_ALL, docType);
        executeCommand(statement);
    }

    private void insertTemplatesSignature(String currentTemplatesSignature) {
        executeCommand(ContentStore.STATEMENT_INSERT_TEMPLATES_SIGNATURE, currentTemplatesSignature);
    }

    @Override
    public DocumentList<DocumentModel> query(String sql) {
        activateOnCurrentThread();
        OResultSet results = db.query(sql);
        return DocumentList.wrap(results);
    }

    @Override
    public DocumentList<DocumentModel> query(String sql, Object... args) {
        activateOnCurrentThread();
        OResultSet results = db.command(sql, args);
        return DocumentList.wrap(results);
    }

    @Override
    public void executeCommand(String query, Object... args) {
        activateOnCurrentThread();
        db.command(query, args);
    }

    @Override
    public Set<String> getTags() {
        DocumentList<DocumentModel> docs = this.getAllTagsFromPublishedPosts();
        Set<String> result = new HashSet<>();
        for (DocumentModel document : docs) {
            String[] tags = document.getTags();
            Collections.addAll(result, tags);
        }
        return result;
    }

    @Override
    public Set<String> getAllTags() {
        Set<String> result = new HashSet<>();
        for (String docType : DocumentTypes.getDocumentTypes()) {
            String statement = String.format(ContentStore.STATEMENT_GET_TAGS_BY_DOCTYPE, docType);
            DocumentList<DocumentModel> docs = query(statement);
            for (DocumentModel document : docs) {
                String[] tags = document.getTags();
                Collections.addAll(result, tags);
            }
        }
        return result;
    }

    private void createDocType(final OSchema schema) {
        logger.debug("Create document class");

        OClass page = schema.createClass(Schema.DOCUMENTS);
        page.createProperty(ModelAttributes.SHA1, OType.STRING).setNotNull(true);
        page.createIndex(Schema.DOCUMENTS + "sha1Index", OClass.INDEX_TYPE.NOTUNIQUE, ModelAttributes.SHA1);
        page.createProperty(ModelAttributes.SOURCE_URI, OType.STRING).setNotNull(true);
        page.createIndex(Schema.DOCUMENTS + "sourceUriIndex", OClass.INDEX_TYPE.UNIQUE, ModelAttributes.SOURCE_URI);
        page.createProperty(ModelAttributes.CACHED, OType.BOOLEAN).setNotNull(true);
        page.createIndex(Schema.DOCUMENTS + "cachedIndex", OClass.INDEX_TYPE.NOTUNIQUE, ModelAttributes.CACHED);
        page.createProperty(ModelAttributes.RENDERED, OType.BOOLEAN).setNotNull(true);
        page.createIndex(Schema.DOCUMENTS + "renderedIndex", OClass.INDEX_TYPE.NOTUNIQUE, ModelAttributes.RENDERED);
        page.createProperty(ModelAttributes.STATUS, OType.STRING).setNotNull(true);
        page.createIndex(Schema.DOCUMENTS + "statusIndex", OClass.INDEX_TYPE.NOTUNIQUE, ModelAttributes.STATUS);
        page.createProperty(ModelAttributes.TYPE, OType.STRING).setNotNull(true);
        page.createIndex(Schema.DOCUMENTS + "typeIndex", OClass.INDEX_TYPE.NOTUNIQUE, ModelAttributes.TYPE);

    }

    private void createSignatureType(OSchema schema) {
        OClass signatures = schema.createClass(Schema.SIGNATURES);
        signatures.createProperty(ModelAttributes.SHA1, OType.STRING).setNotNull(true);
        signatures.createIndex("sha1Idx", OClass.INDEX_TYPE.UNIQUE, ModelAttributes.SHA1);
    }

    public void updateAndClearCacheIfNeeded(boolean needed, File templateFolder) {

        boolean clearCache = needed;

        if (!needed) {
            clearCache = updateTemplateSignatureIfChanged(templateFolder);
        }

        if (clearCache) {
            deleteAllDocumentTypes();
            this.updateSchema();
        }
    }

    private boolean updateTemplateSignatureIfChanged(File templateFolder) {
        boolean templateSignatureChanged = false;

        DocumentList<DocumentModel> docs = this.getSignaturesForTemplates();
        String currentTemplatesSignature;
        try {
            currentTemplatesSignature = FileUtil.sha1(templateFolder);
        } catch (Exception e) {
            currentTemplatesSignature = "";
        }
        if (!docs.isEmpty()) {
            String sha1 = docs.get(0).getSha1();
            if (!sha1.equals(currentTemplatesSignature)) {
                this.updateSignatures(currentTemplatesSignature);
                templateSignatureChanged = true;
            }
        } else {
            // first computation of templates signature
            this.insertTemplatesSignature(currentTemplatesSignature);
            templateSignatureChanged = true;
        }
        return templateSignatureChanged;
    }

    @Override
    public void deleteAllDocumentTypes() {
        for (String docType : DocumentTypes.getDocumentTypes()) {
            try {
                this.deleteAllByDocType(docType);
            } catch (Exception e) {
                // maybe a non existing document type
            }
        }
    }

    public boolean isActive() {
        return db.isActiveOnCurrentThread();
    }

    @Override
    public void addDocument(DocumentModel document) {
        ODocument doc = new ODocument(Schema.DOCUMENTS);
        doc.fromMap(document);
        doc.save();
    }

    protected abstract class Schema {
        static final String DOCUMENTS = "Documents";
        static final String SIGNATURES = "Signatures";
    }

}
