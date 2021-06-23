README for work specific to this branch
=======================================

NOTE: This is still a work in progress.

* SQLite used as contentStore by default.
  [SQLite JDBC Driver](https://github.com/xerial/sqlite-jdbc)
  [About SQLite](https://sqlite.org/about.html)
* The tests are passing using SQLite on Apple M1 Silicon,  Apple Intel and Linux (TeamCity Cloud).
* The tests are passing using OrientDB on Intel and Linux (TeamCity Cloud).
* Two tests are being skipped
  * OvenTest.shouldCrawlRenderAndCopyAssetsorg – Unit test – have not figured how to get Mocks to work with junit5.
  * OvenIntegrationTest.shouldCrawlRenderAndCopyAssetsorg.jbake.app – have not figured out how to drive full build using oven etc.

## GitHub Isuses

[JBake fails to run or build on Apple Silicon #709](https://github.com/jbake-org/jbake/issues/709)


## Notes

* Targetting current LTS versions of Java e.g. using JDK11
  * updating everything to use JUnit5
    – Allows conditional testing using java properties or environment etc.
    - There was already partial usage throughout the project.
* This branch is geared to test SQLIte implementation, but will build with OrientDB.
* If one checks out on an Apple M1 computer, then anything using OrientDB will fail
* There is an environment variable `jbake_db_implementation` that can be used
  to enable/disable the OrientDB related tests from being run. By default it is
  not set, so the OrientDB tests are not run. To enable these to be run do
  ```
  export jbake_db_implementation=OrientDB
  ```
  The SQLite tests always run.
* At runtime, which contentstore to use is controlled via jbake.properties by
  setting `jbake_db_implementation=SQLite` or `jbake_db_implementation=OrientDB`
  The default is SQLite.
* Have introduced ContentStore as an interface to allow changing implementations.
* Have also used JDBC and Groovy SQL and DataSource. This provides a bit more
  independence.
* The DocumentModel is still the main interface between parsers/templates etc
  and ContentStore.  It is translated to a Document which maps to the
  underlying DB table structure.


## Date Mapping

* Stored in content as text with format pattern specified in config file
* MarkupEngine reads this and converts to date using java.util.date, SimpleDateFormatter and format pattern specified in config file
* Sets the DocumentModel date (java.util.date) with this.
* DocumentModel -> Json -> Document
* Document to DB
* DB to Document
* Document -> Json -> DocumentModel
* DocumentModel to templates


## Upgrading to JUnit5

* See [Migrating from JUnit 4 to JUnit 5](https://www.baeldung.com/junit-5-migration)

```

    @Test(expected = RenderingException.class)

    public void shouldRaiseAnException() throws Exception {
        Assertions.assertThrows(Exception.class, () -> {
            //...
        });
    }

Affected classes for Test(expected

 public class ArchiveRendererTest {
 public class Error404RendererTest {
 public class FeedRendererTest {
 public class IndexRendererTest {
 public class SitemapRendererTest {
 public class TagsRendererTest {

```

* Some import statements
```
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
```

* Set environment variable `jbake.db.implementation=OrientDB` to run tests using OrientDb.

* JUnit 5 – Migrating
```
from
-    @ClassRule
-    public static TemporaryFolder folder = new TemporaryFolder();
to
+    @TempDir
+    public static Path folder;

and
from
    String dbPath = folder.newFolder("documents" + System.currentTimeMillis()).getAbsolutePath();
to
    String dbPath = folder.resolve("documents" + System.currentTimeMillis()).toAbsolutePath().toString();


or
from
    validMdFileBasic = folder.newFile("validBasic.md");
to
    validMdFileBasic = folder.resolve("validBasic.md").toFile();

```

### Custom types

* id
* category
* summary
* json


### Sample commands

```
# Run targetted tests
./gradlew clean test --tests *OvenInte* --tests *Sqlite* --tests *CrawlerInte*

# Run tests to test OrientDb
export jbake_db_implementation=OrientDB
./gradlew --no-daemon --no-build-cache --no-configuration-cache --no-configure-on-demand clean test
./gradlew --no-daemon --no-build-cache --no-configuration-cache --no-configure-on-demand clean test --tests "TagsRen*"

# Run tests to test SQLite
export jbake_db_implementation=SQLite
./gradlew clean test
```
