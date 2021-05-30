package org.jbake.template.model;

import org.jbake.app.ContentStoreOrientDb;
import org.jbake.template.ModelExtractor;

import java.util.Map;
import java.util.Set;

public class AllTagsExtractor implements ModelExtractor<Set<String>> {

    @Override
    public Set<String> get(ContentStoreOrientDb db, Map model, String key) {
        return db.getAllTags();
    }

}
