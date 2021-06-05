package org.jbake.template.model;

import org.jbake.db.ContentStore;
import org.jbake.app.DocumentList;
import org.jbake.model.DocumentModel;
import org.jbake.model.DocumentTypes;
import org.jbake.template.ModelExtractor;

import java.util.Map;

public class PublishedContentExtractor implements ModelExtractor<DocumentList> {

    @Override
    public DocumentList get(ContentStore db, Map model, String key) {
        DocumentList<DocumentModel> publishedContent = new DocumentList<>();
        String[] documentTypes = DocumentTypes.getDocumentTypes();
        for (String docType : documentTypes) {
            DocumentList<DocumentModel> query = db.getPublishedContent(docType);
            publishedContent.addAll(query);
        }
        return publishedContent;
    }

}
