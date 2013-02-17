mvn release:clean
mvn release:prepare -DpreparationGoals=clean install
mvn release:perform