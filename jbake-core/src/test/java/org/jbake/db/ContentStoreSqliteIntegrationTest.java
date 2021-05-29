package org.jbake.db;

import groovy.sql.GroovyRowResult;
import groovy.sql.Sql;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import static org.jbake.db.ContentStoreOrientDbTest.DOC_TYPE_POST;
import static org.junit.Assert.assertEquals;

public class ContentStoreSqliteIntegrationTest {

    protected static ContentStoreSqlite contentStoreSqlite;

    @BeforeClass
    public static void setUpClass() throws Exception {
        contentStoreSqlite = new ContentStoreSqlite();
        contentStoreSqlite.createTables();
    }

    @AfterClass
    public static void cleanUpClass() {
//        db.close();
    }

    @Before
    public void setUp() {
        contentStoreSqlite.createTables();
    }

    @After
    public void tearDown() {
//        db.dropTables();
    }

    private Sql getDb() {
        return contentStoreSqlite.getDb();
    }

    @Test
    public void confirmTablesExist() throws Exception {
        GroovyRowResult result = getDb().firstRow("select count(*) numTables from sqlite_master where type = 'table'");
        assertEquals(2, result.getProperty("numTables"));
    }

}
