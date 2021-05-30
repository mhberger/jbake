package org.jbake.app;

import org.jbake.model.DocumentModel;

import java.util.Set;

public interface ContentStore {
    String STATEMENT_GET_PUBLISHED_POST_BY_TYPE_AND_TAG = "select * from Documents where status='published' and type='%s' and ? in tags order by date desc";
    String STATEMENT_GET_DOCUMENT_STATUS_BY_DOCTYPE_AND_URI = "select sha1,rendered from Documents where sourceuri=?";
    String STATEMENT_GET_PUBLISHED_COUNT = "select count(*) as count from Documents where status='published' and type='%s'";
    String STATEMENT_MARK_CONTENT_AS_RENDERD = "update Documents set rendered=true where rendered=false and type='%s' and sourceuri='%s' and cached=true";
    String STATEMENT_DELETE_DOCTYPE_BY_SOURCEURI = "delete from Documents where sourceuri=?";
    String STATEMENT_GET_UNDRENDERED_CONTENT = "select * from Documents where rendered=false order by date desc";
    String STATEMENT_GET_SIGNATURE_FOR_TEMPLATES = "select sha1 from Signatures where key='templates'";
    String STATEMENT_GET_TAGS_FROM_PUBLISHED_POSTS = "select tags from Documents where status='published' and type='post'";
    String STATEMENT_GET_ALL_CONTENT_BY_DOCTYPE = "select * from Documents where type='%s' order by date desc";
    String STATEMENT_GET_PUBLISHED_CONTENT_BY_DOCTYPE = "select * from Documents where status='published' and type='%s' order by date desc";
    String STATEMENT_GET_PUBLISHED_POSTS_BY_TAG = "select * from Documents where status='published' and type='post' and ? in tags order by date desc";
    String STATEMENT_GET_TAGS_BY_DOCTYPE = "select tags from Documents where status='published' and type='%s'";
    String STATEMENT_INSERT_TEMPLATES_SIGNATURE = "insert into Signatures(key,sha1) values('templates',?)";
    String STATEMENT_DELETE_ALL = "delete from Documents where type='%s'";
    String STATEMENT_UPDATE_TEMPLATE_SIGNATURE = "update Signatures set sha1=? where key='templates'";
    String STATEMENT_GET_DOCUMENT_COUNT_BY_TYPE = "select count(*) as count from Documents where type='%s'";

    void updateSchema();

    long getDocumentCount(String docType);

    long getPublishedCount(String docType);

    DocumentList<DocumentModel> getDocumentByUri(String uri);

    DocumentList<DocumentModel> getDocumentStatus(String uri);

    DocumentList<DocumentModel> getPublishedPosts();

    DocumentList<DocumentModel> getPublishedPosts(boolean applyPaging);

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
