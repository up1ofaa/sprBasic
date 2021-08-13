#!/bin/sh
#------------------------------------------------------------------------------
#    파   일   명 : NBBMBND822J.sh
#    작   성   일 : 2019.10.25
#    작   성   자 : 손인영
#    설        명 : 안내장 일괄 발송
#------------------------------------------------------------------------------
#    <참고사항>
#     사용법 :
#       %> ./NBBMBND822J.sh 20191101
#------------------------------------------------------------------------------
#  ↓ Do not modify. 수정하지 마세요.
#------------------------------------------------------------------------------
#BASEDIR=$(dirname "$0")
SHLLNAME=$(basename "$0")
#BASEDIR=/hli_appl/appl/src/nbm/WEB-INF
BASEDIR=/app/sw/was/servers/node11/webapps/nbm/WEB-INF
echo $SHLLNAME
# ------------------------------------
#CLASSPATH=/hli_appl/appl/src/nbm/WEB-INF/classes:/hli_appl/appl/src/nbm/WEB-INF/lib/*
CLASSPATH=/app/sw/was/servers/node11/webapps/nbm/WEB-INF/classes:/app/sw/was/servers/node11/webapps/nbm/WEB-INF/lib/*
#echo "CLASSPATH $CLASSPATH"
# ------------------------------------
#JAVA_BIN=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.212.b04-0.el7_6.x86_64/jre/bin/java
JAVA_BIN=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.222.b03-1.el7.x86_64/jre/bin/java
OPT="-cp $CLASSPATH -Xmx512M"
#echo "JAVABIN $JAVA_BIN"
#------------------------------------------------------------------------------
#  ↑ Do not modify. 수정하지 마세요.
#------------------------------------------------------------------------------
# 배치명 
BATCHNAME=NBBMBND822J
# 배치로그디렉토리
#LOGDIR=/hli_appl/appl/logs/batch/
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
else
ARG1=$1
fi
arg1="${ARG1#*arg1=}"
echo "ARG1  : $ARG1"
echo "arg1  : $arg1"
echo "BSEDT : $BSEDT"
FILEPATH=/app/data/hli_data/and/recv/MBCNSLHS_$BSEDT.dat
#echo "FILEPATH : $FILEPATH"
# ------------------------------------
# CommandLineJobRunner 배치명 파라미터1 파라미터2 ...
# 로그파일 : /hli_appl/appl/logs/batch/BATCHNAME/BATCHNAME.yyyymmddhhmiss.log에 저장
# 파일위치 : /hli_data/and/recv/MBCNSLHS_BSEDT.dat 
#if [ -f "$/hli_data/and/recv/MBCNSLHS_$BSEDT.dat" ];
#if [ -f "$/app/data/hli_data/and/recv/MBCNSLHS_$BSEDT.dat" ]; ##파일이 정규파일이고 존재해야지
if [ -f "$FILEPATH" ];
then
echo "$FILEPATH exists"
nohup $JAVA_BIN $OPT com.hanwhalife.nbm.batch.runner.CommandLineJobRunner ${BATCHNAME} shllName=${SHLLNAME} arg1=${arg1} >> ${LOGDIR}/${BATCHNAME}.${DATE}.log 2>&1 &
else
echo "NBBMBND822J file not found.."
fi
