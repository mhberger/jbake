package org.jbake.template.model;

import org.jbake.app.ContentStoreOrientDb;
import org.jbake.template.ModelExtractor;

import java.util.Map;

public class DBExtractor implements ModelExtractor<ContentStoreOrientDb> {

    @Override
    public ContentStoreOrientDb get(ContentStoreOrientDb db, Map model, String key) {
        return db;
    }

}
