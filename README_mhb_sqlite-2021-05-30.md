README for work specific to this branch
=======================================

NOTE: This is still rough as guts and is a work in progress.

## GitHub Isuses

[JBake fails to run or build on Apple Silicon #709](https://github.com/jbake-org/jbake/issues/709)


## Notes

* This branch is geared to test SQLIte implementation.
* If one checks out on an M1 computer, then anything using OrientDB will fail
* So we run focussed tests.
* There is a new new property `db.implementation` that can be used to detect
  which type of ContentStore is being used.


### Sample commands

```
# Run tests
./gradlew clean test --tests *OvenInte* --tests *Sqlite* --tests *CrawlerInte*
```
