package org.jbake.db;

import org.jbake.app.DocumentList;
import org.jbake.model.DocumentModel;

import java.util.Set;

public class ContentStoreSqlite implements ContentStore {
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
