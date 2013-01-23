#! /bin/sh 

ps aux|grep 'netty-echo-server'|grep -v 'grep'|awk '{print $2}' | xargs kill -9