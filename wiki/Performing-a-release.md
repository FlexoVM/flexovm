```
mvn release:clean
mvn -Dgpg.keyname=<key> release:prepare
mvn -Dgpg.keyname=<key> release:perform
```