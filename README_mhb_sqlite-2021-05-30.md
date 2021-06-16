README for work specific to this branch
=======================================

NOTE: This is still rough as guts and is a work in progress.

* The tests are failing using SQLite on M1 Silicon – need to fix selection of
  db.
* Tests failing on Intel when running with SQLite or OrientDB..
* At runtime, content is generated on M1 Silicon, but dates are
  out (Timezone related)
* Currently working on how the DocumentModel is persisted via Document
  and in Documents table.


## GitHub Isuses

[JBake fails to run or build on Apple Silicon #709](https://github.com/jbake-org/jbake/issues/709)


## Notes

* Targetting current LTS versions of Java e.g. using JDK11
  * updating everything to use JUnit5 – allows conditional testing using java
    properties or environment etc
* This branch is geared to test SQLIte implementation.
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

```

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
