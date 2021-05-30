package org.jbake.app;

import org.jbake.model.DocumentModel;

import java.util.Set;

public interface ContentStore {
    void updateSchema();

    long getDocumentCount(String docType);

    long getPublishedCount(String docType);

    DocumentList<DocumentModel> getDocumentByUri(String uri);

    DocumentList<DocumentModel> getDocumentStatus(String uri);

    DocumentList<DocumentModel> getPublishedPosts();

    DocumentList<DocumentModel> getPublishedPostsByTag(String tag);

    DocumentList<DocumentModel> getPublishedDocumentsByTag(String tag);

    DocumentList<DocumentModel> getPublishedPages();

    DocumentList<DocumentModel> getPublishedContent(String docType);

    DocumentList<DocumentModel> getAllContent(String docType);

    DocumentList<DocumentModel> getAllContent(String docType, boolean applyPaging);

    DocumentList<DocumentModel> getUnrenderedContent();

    void deleteContent(String uri);

    void markContentAsRendered(DocumentModel document);

    void deleteAllByDocType(String docType);

    Set<String> getTags();

    Set<String> getAllTags();

    void addDocument(DocumentModel document);

    DocumentList<DocumentModel> getPublishedContent(String docType, boolean applyPaging);

    DocumentList<DocumentModel> getAllTagsFromPublishedPosts();

    DocumentList<DocumentModel> query(String sql);

    DocumentList<DocumentModel> query(String sql, Object... args);

    void executeCommand(String query, Object... args);

    void deleteAllDocumentTypes();
}
