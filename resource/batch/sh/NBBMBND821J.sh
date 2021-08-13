#!/bin/sh
#------------------------------------------------------------------------------
#    파   일   명 : NBBMBND821J.sh
#    작   성   일 : 2019.10.25
#    작   성   자 : 손인영
#    설        명 : 안내장 일괄 발송
#------------------------------------------------------------------------------
#    <참고사항>
#     사용법 :
#       %> ./NBBMBND821J.sh 20191101
#------------------------------------------------------------------------------
#  ↓ Do not modify. 수정하지 마세요.
#------------------------------------------------------------------------------
SHLLNAME=$(basename "$0")
BASEDIR=/app/sw/was/servers/node11/webapps/nbm/WEB-INF
echo $SHLLNAME
# ------------------------------------
CLASSPATH=/app/sw/was/servers/node11/webapps/nbm/WEB-INF/classes:/app/sw/was/servers/node11/webapps/nbm/WEB-INF/lib/*
# ------------------------------------
JAVA_BIN=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.222.b03-1.el7.x86_64/jre/bin/java
OPT="-cp $CLASSPATH -Xmx512M"
#------------------------------------------------------------------------------
#  ↑ Do not modify. 수정하지 마세요.
#------------------------------------------------------------------------------
# hostname 읽어오기 (hostname에 따라 개발hli_nbmdev/운영hli_nbm1, hli_nbm2 구분) 
#------------------------------------------------------------------------------
HOSTNAME_DEV=vdevnbm
read HOSTNAME</etc/hostname
echo "HOSTNAME     :  $HOSTNAME.."
echo "HOSTNAME_DEV :  $HOSTNAME_DEV .."
#------------------------------------------------------------------------------

# 배치명 
BATCHNAME=NBBMBND821J
# 배치로그디렉토리
LOGDIR=/app/logs/batch/
if [ ! -d "$LOGDIR$BATCHNAME" ];
then
    mkdir -p $LOGDIR$BATCHNAME
fi
LOGDIR=$LOGDIR$BATCHNAME
echo "LOGDIR $LOGDIR"
# ------------------------------------
# 배치수행 커맨드
echo "$SHLLNAME started..."
DATE=`date +%Y%m%d%H%M%S`
BSEDT=`date +%Y%m%d`
# ------------------------------------
# 첫번째 파라미터 널 체크
if [ -z $1 ];
then
ARG1="arg1=$BSEDT"
ARG2="arg2=$BSEDT"
else
ARG1=$1
ARG2=$2
#-------------------------------
# arg1=은 다음글자 읽어오기
BSEDT="${ARG1#*arg1=}"
fi

arg1="${ARG1#*arg1=}"
arg2="${ARG2#*arg2=}"

echo "ARG1  : $ARG1"
echo "ARG2  : $ARG2"
echo "arg1  : $arg1"
echo "arg2  : $arg2"
# ------------------------------------
# CommandLineJobRunner 배치명 파라미터1 파라미터2 ...
# 로그파일 : /hli_appl/appl/logs/batch/BATCHNAME/BATCHNAME.yyyymmddhhmiss.log에 저장

##nohup $JAVA_BIN $OPT com.hanwhalife.nbm.batch.runner.CommandLineJobRunner ${BATCHNAME} shllName=${SHLLNAME} ${ARG1} ${ARG2} >> ${LOGDIR}/${BATCHNAME}.${DATE}.log 2>&1 &
$JAVA_BIN $OPT com.hanwhalife.nbm.batch.runner.CommandLineJobRunner ${BATCHNAME} shllName=${SHLLNAME} arg1=${arg1} arg2=${arg2} >> ${LOGDIR}/${BATCHNAME}.${DATE}.log 2>&1
echo "$BATCHNAME  END ........"  >> ${LOGDIR}/${BATCHNAME}.${DATE}.log 2>&1

#-----------------------------------------------
# $2 : fep 서버로 파일전송
# $2 : 개발,QA IP : 10.10.153.178
#      운영    IP : 10.10.163.26, 10.10.163.27
#-----------------------------------------------
if [ $HOSTNAME != $HOSTNAME_DEV ]
then
echo "운영"
echo " 중앙신용정보 파일전송 운영 START..... "  >> ${LOGDIR}/${BATCHNAME}.${DATE}.log 2>&1

scp -P 41 /app/data/hli_data/and/send/NBBAT_JACS_BMBNDEND_${BSEDT}.dat wasadmin@10.10.163.26:/app/data/WAS/BAT/source/RECVROOT/NBBAT/JACS
echo " scp -P 41 /app/data/hli_data/and/send/NBBAT_JACS_BMBNDEND_$BSEDT.dat wasadmin@10.10.163.26:/app/data/WAS/BAT/source/RECVROOT/NBBAT/JACS "  >> ${LOGDIR}/${BATCHNAME}.${DATE}.log 2>&1

else

echo "개발"
echo " 중앙신용정보 파일전송 개발 START..... "  >> ${LOGDIR}/${BATCHNAME}.${DATE}.log 2>&1
scp -P 41 /app/data/hli_data/and/send/NBBAT_JACS_BMBNDEND_${BSEDT}.dat wasadmin@10.10.153.178:/app/data/WAS/BAT/source/RECVROOT/NBBAT/JACS
echo " scp -P 41 /app/data/hli_data/and/send/NBBAT_JACS_BMBNDEND_$BSEDT.dat wasadmin@10.10.153.178:/app/data/WAS/BAT/source/RECVROOT/NBBAT/JACS "  >> ${LOGDIR}/${BATCHNAME}.${DATE}.log 2>&1

fi

echo " 중앙신용정보 파일전송 END ..... "  >> ${LOGDIR}/${BATCHNAME}.${DATE}.log 2>&1
