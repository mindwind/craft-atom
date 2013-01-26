#! /bin/sh 

RP=/export/test/craft.atom.test
CP=$RP:$RP/cfg

for f in $RP/lib/*.jar; do
  CP=$CP:$f;
done

export CP

JAVA_OPTS='-Xms256m -Xmx256m -Dservice=craft-echo-server -Dfair.mode=true -Dio.pool=4 -Dexecutor.pool=4 -Dread.buffer.size=1024'
nohup java -server $JAVA_OPTS -classpath $CP org.craft.atom.test.nio.CraftEchoServer >/dev/null 2>&1 &