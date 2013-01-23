#! /bin/sh 

ps aux|grep 'craft-echo-server'|grep -v 'grep'|awk '{print $2}' | xargs kill -9