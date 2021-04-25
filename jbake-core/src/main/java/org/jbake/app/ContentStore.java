package org.jbake.app;

import com.orientechnologies.orient.core.record.impl.ODocument;

import java.io.File;
import java.util.Map;
import java.util.Set;

public interface ContentStore {
    void startup();

    long getStart();

    void setStart(int start);

    long getLimit();

    void setLimit(int limit);

    void resetPagination();

    void updateSchema();

    void close();

    void shutdown();

    void drop();

    ODocument mergeDocument(Map<String, ? extends Object> incomingDocMap);

    long getDocumentCount(String docType);

    long getPublishedCount(String docType);

    /*
     * In fact, the URI should be the only input as there can only be one document at given URI; but the DB is split per document type for some reason.
     */
    DocumentList getDocumentByUri(String docType, String uri);

    DocumentList getDocumentStatus(String docType, String uri);

    DocumentList getPublishedPosts();

    DocumentList getPublishedPosts(boolean applyPaging);

    DocumentList getPublishedPostsByTag(String tag);

    DocumentList getPublishedDocumentsByTag(String tag);

    DocumentList getPublishedPages();

    DocumentList getPublishedContent(String docType);

    DocumentList getAllContent(String docType);

    DocumentList getAllContent(String docType, boolean applyPaging);

    DocumentList getUnrenderedContent(String docType);

    void deleteContent(String docType, String uri);

    void markContentAsRendered(String docType);

    void deleteAllByDocType(String docType);

    Set<String> getTags();

    Set<String> getAllTags();

    void updateAndClearCacheIfNeeded(boolean needed, File templateFolder);

    boolean isActive();
}
