README for work specific to this branch
=======================================

NOTE: This is still rough as guts and is a work in progress.

## GitHub Isuses

[JBake fails to run or build on Apple Silicon #709](https://github.com/jbake-org/jbake/issues/709)


## Notes

* Targetting current LTS versions of Java e.g. using JDK11
  * updating everything to use JUnit5 – allows conditional testing using java properties or environment etc
* This branch is geared to test SQLIte implementation.
* If one checks out on an M1 computer, then anything using OrientDB will fail
* So we run focussed tests.
* There is a new new property `db.implementation` that can be used to detect
  which type of ContentStore is being used.


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


### Sample commands

```
# Run tests
./gradlew clean test --tests *OvenInte* --tests *Sqlite* --tests *CrawlerInte*

# Run tests to test OrientDb
export jbake_db_implementation=OrientDB
./gradlew --no-daemon --no-build-cache --no-configuration-cache --no-configure-on-demand clean test
./gradlew --no-daemon --no-build-cache --no-configuration-cache --no-configure-on-demand clean test --tests "TagsRen*"

# Run tests to test SQLite
export jbake_db_implementation=SQLite
./gradlew clean test
```
