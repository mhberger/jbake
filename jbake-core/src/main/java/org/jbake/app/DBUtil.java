package org.jbake.app;

import com.orientechnologies.orient.core.db.record.OTrackedList;
import com.orientechnologies.orient.core.sql.executor.OResult;
import org.jbake.app.configuration.JBakeConfiguration;
import org.jbake.db.ContentStore;
import org.jbake.db.ContentStoreOrientDb;
import org.jbake.model.DocumentModel;

public class DBUtil {
    private static ContentStoreOrientDb contentStore;

    @Deprecated
    public static ContentStore createDataStore(final String type, String name) {
        if (contentStore == null) {
            contentStore = new ContentStoreOrientDb(type, name);
        }
        return contentStore;
    }

    @Deprecated
    public static void updateSchema(final ContentStoreOrientDb db) {
        db.updateSchema();
    }

    public static ContentStoreOrientDb createDataStore(JBakeConfiguration configuration) {
        if (contentStore == null) {
            contentStore = new ContentStoreOrientDb(configuration.getDatabaseStore(), configuration.getDatabasePath());
        }

        return contentStore;
    }

    public static void closeDataStore() {
        contentStore = null;
    }

    public static DocumentModel documentToModel(OResult doc) {
        DocumentModel result = new DocumentModel();

        for (String key : doc.getPropertyNames()) {
            result.put(key, doc.getProperty(key));
        }
        return result;
    }

    /**
     * Converts a DB list into a String array
     *
     * @param entry Entry input to be converted
     * @return input entry as String[]
     */
    @SuppressWarnings("unchecked")
    public static String[] toStringArray(Object entry) {
        if (entry instanceof String[]) {
            return (String[]) entry;
        } else if (entry instanceof OTrackedList) {
            OTrackedList<String> list = (OTrackedList<String>) entry;
            return list.toArray(new String[list.size()]);
        }
        return new String[0];
    }

}
