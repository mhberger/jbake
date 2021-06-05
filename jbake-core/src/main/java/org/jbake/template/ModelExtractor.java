package org.jbake.template;

import org.jbake.db.ContentStore;


import java.util.Map;


/**
 * @param <T> the type of data returned by this model extractor
 * @author ndx
 */
public interface ModelExtractor<T> {

    T get(ContentStore db, Map model, String key);

}
