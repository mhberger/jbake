package org.jbake.db;

import org.apache.commons.vfs2.util.Os;
import org.jbake.TestUtils;
import org.jbake.app.DBUtil;
import org.jbake.app.configuration.ConfigUtil;
import org.jbake.app.configuration.DefaultJBakeConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledIfEnvironmentVariable(named = "jbake_db_implementation", matches = "OrientDB")
public abstract class ContentStoreOrientDbIntegrationTest {

    @TempDir
    public static Path folder;
    protected static ContentStoreOrientDb db;
    protected static DefaultJBakeConfiguration config;
    protected static StorageType storageType = StorageType.MEMORY;
    protected static File sourceFolder;

    @BeforeAll
    public static void setUpClass() throws Exception {

        sourceFolder = TestUtils.getTestResourcesAsSourceFolder();
        assertTrue(sourceFolder.exists(), "Cannot find sample data structure!");

        config = (DefaultJBakeConfiguration) new ConfigUtil().loadConfig(sourceFolder);
        config.setSourceFolder(sourceFolder);

        assertEquals(".html", config.getOutputExtension());
        config.setDatabaseStore(storageType.toString());
        String dbPath = folder.resolve("documents" + System.currentTimeMillis()).toAbsolutePath().toString();

        // setting the database path with a colon creates an invalid url for OrientDB.
        // only one colon is expected. there is no documentation about proper url path for windows available :(
        if (Os.isFamily(Os.OS_FAMILY_WINDOWS)) {
            dbPath = dbPath.replace(":","");
        }
        config.setDatabasePath(dbPath);
        db = DBUtil.createDataStore(config);
    }

    @AfterAll
    public static void cleanUpClass() {
        db.close();
        db.shutdown();
    }

    @BeforeEach
    public void setUp() {
        db.startup();
    }

    @AfterEach
    public void tearDown() {
        db.drop();
    }

    protected enum StorageType {
        MEMORY, PLOCAL;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

}
