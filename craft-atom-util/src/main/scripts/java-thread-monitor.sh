#!/bin/bash


# author: mindwind       
# date  : Apr 10, 2014

function monitor()
{
    # get load avg last 1 minute
    loadavg1=$(cat /proc/loadavg | awk '{print $1}')


    # load is low (<30), it is normal so we exit
    if [ $(echo "$loadavg1 < 30"|bc) = 1 ]; then
        echo load=$loadavg1 is normal
        return
    fi


    # load is high (>=30), find top 3 java process id
    runtime=$(date "+%Y%m%d-%H%M%S")
    pids=$(ps auxw|grep 'java'|grep -v 'grep'|sort -rn -k3|head -3|awk '{print $2}')
    for p in $pids
    do
        echo "jstack pid=$p runtime=$runtime"
        jstack -F $p > $p-stack-$runtime.log
        top -p $p -H -b -n 1 > $p-top-$runtime.log
    done
}

while [ "1" = "1" ]
do
    monitor
    sleep 10
done