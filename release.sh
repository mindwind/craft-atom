mvn release:clean
mvn -Darguments="-DskipTests=true" release:prepare
mvn release:perform