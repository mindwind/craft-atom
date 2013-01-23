#! /bin/sh 

ps aux|grep 'mina-echo-server'|grep -v 'grep'|awk '{print $2}' | xargs kill -9