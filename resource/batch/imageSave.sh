#!/usr/bin/env bash

BASEDIR=$(dirname "$0")
#. $BASEDIR/env.sh

BATCHNAME=imageSave
# ------------------------------------
# 배치수행 커맨드
echo "$SHLLNAME started..."
NBM_HOST_IP1=10.10.11.11
NBM_HOST_IP2=10.10.11.10
NBM_DEV_IP=10.10.153.190
echo "$NBM_DEV_IP"
# ------------------------------------
if [ -z $1 ] ; 
then
 ARG1=10.10.153.190
 echo "$ARG1"   
else
  ARG1=$1
  echo "$ARG1"
fi
if [ -z $2 ] ;
then
 ARG2=FAKEFILE.TXT
 echo "$ARG2"
else
 ARG2=$2
 echo "$ARG2"
fi
 echo "if-END"
#----------------------------------
#--------CommandLineJobRunner------
hostIp="${ARG1#*arg1=}"
fileNm="${ARG2#*arg2=}"
echo "$hostIp"
if [ $hostIp = $NBM_DEV_IP ] ;
then 
#scp -P 41 /app/sw/was/servers/node11/nbmImg/$fileNm jboss@10.10.153.190:/app/sw/was/servers/node11/nbmImg1
cp /app/sw/was/servers/node11/nbmImg/$fileNm /app/sw/was/servers/node11/nbmImg1/.
echo " scp -P 41 /app/sw/was/servers/node11/nbmImg/$fileNm jboss@10.10.153.190:/app/sw/was/servers/node11/nbmImg1 "
fi
if [ $hostIp = $NBM_HOST_IP1 ] ;
then
scp -P 41 /app/sw/was/servers/nod11/nbmImg/$fileNm jboss@10.10.11.10:/app/sw/was/servers/node11/nbmImg
echo " scp -P 41 /app/sw/was/servers/node11/nbmImg/$fileNm jboss@10.10.11.10:/app/sw/was/servers/node11/nbmImg "
fi
if [ $hostIp = $NBM_HOST_IP2 ] ;
then
scp -P 41 /app/sw/was/servers/nod11/nbmImg/$fileNm jboss@10.10.11.11:/app/sw/was/servers/node11/nbmImg
echo " scp -P 41 /app/sw/was/servers/node11/nbmImg/$fileNm jboss@10.10.11.11:/app/sw/was/servers/node11/nbmImg "
fi
echo "end imageSave.sh"
