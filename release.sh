mvn clean
mvn install
mvn javadoc:jar
mvn source:jar
mvn release:clean
mvn release:prepare
mvn release:perform
