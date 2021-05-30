package org.jbake.app;

import org.jbake.model.DocumentModel;

import java.util.Set;

public abstract class ContentStore {
    protected static final String STATEMENT_GET_PUBLISHED_POST_BY_TYPE_AND_TAG = "select * from Documents where status='published' and type='%s' and ? in tags order by date desc";
    protected static final String STATEMENT_GET_DOCUMENT_STATUS_BY_DOCTYPE_AND_URI = "select sha1,rendered from Documents where sourceuri=?";
    protected static final String STATEMENT_GET_PUBLISHED_COUNT = "select count(*) as count from Documents where status='published' and type='%s'";
    protected static final String STATEMENT_MARK_CONTENT_AS_RENDERD = "update Documents set rendered=true where rendered=false and type='%s' and sourceuri='%s' and cached=true";
    protected static final String STATEMENT_DELETE_DOCTYPE_BY_SOURCEURI = "delete from Documents where sourceuri=?";
    protected static final String STATEMENT_GET_UNDRENDERED_CONTENT = "select * from Documents where rendered=false order by date desc";
    protected static final String STATEMENT_GET_SIGNATURE_FOR_TEMPLATES = "select sha1 from Signatures where key='templates'";
    protected static final String STATEMENT_GET_TAGS_FROM_PUBLISHED_POSTS = "select tags from Documents where status='published' and type='post'";
    protected static final String STATEMENT_GET_ALL_CONTENT_BY_DOCTYPE = "select * from Documents where type='%s' order by date desc";
    protected static final String STATEMENT_GET_PUBLISHED_CONTENT_BY_DOCTYPE = "select * from Documents where status='published' and type='%s' order by date desc";
    protected static final String STATEMENT_GET_PUBLISHED_POSTS_BY_TAG = "select * from Documents where status='published' and type='post' and ? in tags order by date desc";
    protected static final String STATEMENT_GET_TAGS_BY_DOCTYPE = "select tags from Documents where status='published' and type='%s'";
    protected static final String STATEMENT_INSERT_TEMPLATES_SIGNATURE = "insert into Signatures(key,sha1) values('templates',?)";
    protected static final String STATEMENT_DELETE_ALL = "delete from Documents where type='%s'";
    protected static final String STATEMENT_UPDATE_TEMPLATE_SIGNATURE = "update Signatures set sha1=? where key='templates'";
    protected static final String STATEMENT_GET_DOCUMENT_COUNT_BY_TYPE = "select count(*) as count from Documents where type='%s'";

    public abstract void updateSchema();

    public abstract long getDocumentCount(String docType);

    public abstract long getPublishedCount(String docType);

    public abstract DocumentList<DocumentModel> getDocumentByUri(String uri);

    public abstract DocumentList<DocumentModel> getDocumentStatus(String uri);

    public abstract DocumentList<DocumentModel> getPublishedPosts();

    public abstract DocumentList<DocumentModel> getPublishedPostsByTag(String tag);

    public abstract DocumentList<DocumentModel> getPublishedDocumentsByTag(String tag);

    public abstract DocumentList<DocumentModel> getPublishedPages();

    public abstract DocumentList<DocumentModel> getPublishedContent(String docType);

    protected abstract DocumentList<DocumentModel> getPublishedContent(String docType, boolean applyPaging);

    public abstract DocumentList<DocumentModel> getAllContent(String docType);

    public abstract DocumentList<DocumentModel> getAllContent(String docType, boolean applyPaging);

    protected abstract DocumentList<DocumentModel> getAllTagsFromPublishedPosts();

    public abstract DocumentList<DocumentModel> getUnrenderedContent();

    public abstract void deleteContent(String uri);

    public abstract void markContentAsRendered(DocumentModel document);

    public abstract void deleteAllByDocType(String docType);

    protected abstract DocumentList<DocumentModel> query(String sql);

    protected abstract DocumentList<DocumentModel> query(String sql, Object... args);

    protected abstract void executeCommand(String query, Object... args);

    public abstract Set<String> getTags();

    public abstract Set<String> getAllTags();

    protected abstract void deleteAllDocumentTypes();

    public abstract void addDocument(DocumentModel document);
}
