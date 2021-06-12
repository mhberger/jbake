package org.jbake.db;

import org.apache.commons.vfs2.util.Os;
import org.jbake.TestUtils;
import org.jbake.app.DBUtil;
import org.jbake.app.configuration.ConfigUtil;
import org.jbake.app.configuration.DefaultJBakeConfiguration;
import org.jbake.db.ContentStoreOrientDb;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.rules.TemporaryFolder;

import java.io.File;

@EnabledIfSystemProperty(named = "jbake.db.implementation", matches = "OrientDB")
public abstract class ContentStoreOrientDbIntegrationTest {

    @ClassRule
    public static TemporaryFolder folder = new TemporaryFolder();
    protected static ContentStoreOrientDb db;
    protected static DefaultJBakeConfiguration config;
    protected static StorageType storageType = StorageType.MEMORY;
    protected static File sourceFolder;

    @BeforeClass
    public static void setUpClass() throws Exception {

        sourceFolder = TestUtils.getTestResourcesAsSourceFolder();
        Assert.assertTrue("Cannot find sample data structure!", sourceFolder.exists());

        config = (DefaultJBakeConfiguration) new ConfigUtil().loadConfig(sourceFolder);
        config.setSourceFolder(sourceFolder);

        Assert.assertEquals(".html", config.getOutputExtension());
        config.setDatabaseStore(storageType.toString());
        String dbPath = folder.newFolder("documents" + System.currentTimeMillis()).getAbsolutePath();

        // setting the database path with a colon creates an invalid url for OrientDB.
        // only one colon is expected. there is no documentation about proper url path for windows available :(
        if (Os.isFamily(Os.OS_FAMILY_WINDOWS)) {
            dbPath = dbPath.replace(":","");
        }
        config.setDatabasePath(dbPath);
        db = DBUtil.createDataStore(config);
    }

    @AfterClass
    public static void cleanUpClass() {
        db.close();
        db.shutdown();
    }

    @Before
    public void setUp() {
        db.startup();
    }

    @After
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
