package org.jbake.template.model;

import org.jbake.app.ContentStoreOrientDb;
import org.jbake.app.DocumentList;
import org.jbake.template.ModelExtractor;

import java.util.Map;

public class PublishedPagesExtractor implements ModelExtractor<DocumentList> {

    @Override
    public DocumentList get(ContentStoreOrientDb db, Map model, String key) {
        return db.getPublishedPages();
    }

}
