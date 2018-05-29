#!/bin/bash


function startHttp(){
  readLog
  ./httpserver stop
  ./httpserver start

readLog
}


function stopHttp(){
  ./httpserver stop

  readLog
}
function checkHttp(){
  ps aux | grep 'javahttpserver.jar'
}

function readLog(){
 cat server.log
}

function delLog(){
rm -rf server.log
}


echo "1.启动httpserver"
echo "2.停止httpserver"
echo "3.查看Http状态"
echo "4.查看日志"
echo "5.删除日志"
echo "******Enter nothing to exit*****"
read num

case "$num" in
[1] ) (startHttp);;
[2] ) (stopHttp);;
[3] ) (checkHttp);;
[4] ) (readLog);;
[5] ) (delLog);;

*) echo "nothing,exit";;
esac
