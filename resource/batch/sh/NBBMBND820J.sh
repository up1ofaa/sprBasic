#!/bin/sh
#------------------------------------------------------------------------------
#    파   일   명 : NBBMBND820J.sh
#    작   성   일 : 2019.10.25
#    작   성   자 : 손인영
#    설        명 : 안내장 일괄 발송
#------------------------------------------------------------------------------
#    <참고사항>
#     사용법 :
#       %> ./NBBMBND820J.sh 20191101
#------------------------------------------------------------------------------
#  ↓ Do not modify. 수정하지 마세요.
#------------------------------------------------------------------------------
SHLLNAME=$(basename "$0")
BASEDIR=/app/sw/was/servers/node11/webapps/nbm/WEB-INF
echo $SHLLNAME
# ------------------------------------
CLASSPATH=/app/sw/was/servers/node11/webapps/nbm/WEB-INF/classes:/app/sw/was/servers/node11/webapps/nbm/WEB-INF/lib/*
#echo "CLASSPATH $CLASSPATH"
# ------------------------------------
JAVA_BIN=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.222.b03-1.el7.x86_64/jre/bin/java
OPT="-cp $CLASSPATH -Xmx512M"
#echo "JAVABIN $JAVA_BIN"
#------------------------------------------------------------------------------
#  ↑ Do not modify. 수정하지 마세요.
#------------------------------------------------------------------------------
# hostname 읽어오기 (hostname에 따라 개발hli_nbmdev/운영hli_nbm1, hli_nbm2 구분) 
# read 명령어로 경로의 hostname 가져와 변수에 넣기
#------------------------------------------------------------------------------
HOSTNAME_DEV=vdevnbm
read HOSTNAME</etc/hostname
echo "HOSTNAME     :  $HOSTNAME.."
echo "HOSTNAME_DEV :  $HOSTNAME_DEV .."
#------------------------------------------------------------------------------

# 배치명 
BATCHNAME=NBBMBND820J
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
BSEYM=`date +%Y%m`
BSEDT=`date +%Y%m%d`
# ------------------------------------
# 첫번째 파라미터 널 체크
if [ -z $1 ];
then
ARG1="arg1=$BSEYM"
ARG2="arg2=$BSEDT"
else
ARG1=$1
ARG2=$2
#-------------------------------
# arg1=은 다음글자 읽어오기
BSEYM="${ARG1#*arg1=}"
BSEDT="${ARG2#*arg2=}"
fi

arg1=$BSEYM
arg2=$BSEDT
echo "ARG1  : $ARG1"
echo "ARG2  : $ARG2"
echo "arg1  : $arg1"
echo "arg2  : $arg2"
# ------------------------------------
# CommandLineJobRunner 배치명 파라미터1 파라미터2 ...
# 로그파일 : /hli_appl/appl/logs/batch/BATCHNAME/BATCHNAME.yyyymmddhhmiss.log에 저장
# 로그파일 : /app/logs/batch/BATCHNAME/BATCHNAME.yyyymmddhhmiss.log에 저장
echo " ${BATCHNAME} shllName=${SHLLNAME} ${ARG1} ${ARG2}"
$JAVA_BIN $OPT com.hanwhalife.nbm.batch.runner.CommandLineJobRunner ${BATCHNAME} shllName=${SHLLNAME} arg1=${arg1} arg2=${arg2} >> ${LOGDIR}/${BATCHNAME}.${DATE}.log 2>&1
echo "$BATCHNAME  END ........"  >> ${LOGDIR}/${BATCHNAME}.${DATE}.log 2>&1


#-------------------------------------------------
# $2 : fep 개발,QA IP : 10.10.153.178
#      fep 운영    IP : 10.10.163.26, 10.10.163.27
#-------------------------------------------------
if [ $HOSTNAME != $HOSTNAME_DEV ]
then
echo "운영"
scp -P 41 /app/data/hli_data/and/send/NBBAT_JACS_BMBNDMST_${BSEDT}.dat wasadmin@10.10.163.26:/app/data/WAS/BAT/source/RECVROOT/NBBAT/JACS
scp -P 41 /app/data/hli_data/and/send/NBBAT_JACS_BMBNDDTL_${BSEDT}.dat wasadmin@10.10.163.26:/app/data/WAS/BAT/source/RECVROOT/NBBAT/JACS

echo " 중앙신용정보 파일전송 운영 START..... "  >> ${LOGDIR}/${BATCHNAME}.${DATE}.log 2>&1
echo " scp -P 41 /app/data/hli_data/and/send/NBBAT_JACS_BMBNDMST_$BSEDT.dat wasadmin@10.10.163.26:/app/data/WAS/BAT/source/RECVROOT/NBBAT/JACS "  >> ${LOGDIR}/${BATCHNAME}.${DATE}.log 2>&1
echo " scp -P 41 /app/data/hli_data/and/send/NBBAT_JACS_BMBNDDTL_$BSEDT.dat wasadmin@10.10.163.26:/app/data/WAS/BAT/source/RECVROOT/NBBAT/JACS "  >> ${LOGDIR}/${BATCHNAME}.${DATE}.log 2>&1


else

echo "개발"
scp -P 41 /app/data/hli_data/and/send/NBBAT_JACS_BMBNDMST_${BSEDT}.dat wasadmin@10.10.153.178:/app/data/WAS/BAT/source/RECVROOT/NBBAT/JACS
scp -P 41 /app/data/hli_data/and/send/NBBAT_JACS_BMBNDDTL_${BSEDT}.dat wasadmin@10.10.153.178:/app/data/WAS/BAT/source/RECVROOT/NBBAT/JACS

echo "중앙신용정보 파일전송 개발 START..... "  >> ${LOGDIR}/${BATCHNAME}.${DATE}.log 2>&1
echo " scp -P 41 /app/data/hli_data/and/send/NBBAT_JACS_BMBNDMST_$BSEDT.dat wasadmin@10.10.153.178:/app/data/WAS/BAT/source/RECVROOT/NBBAT/JACS "  >> ${LOGDIR}/${BATCHNAME}.${DATE}.log 2>&1
echo " scp -P 41 /app/data/hli_data/and/send/NBBAT_JACS_BMBNDDTL_$BSEDT.dat wasadmin@10.10.153.178:/app/data/WAS/BAT/source/RECVROOT/NBBAT/JACS "  >> ${LOGDIR}/${BATCHNAME}.${DATE}.log 2>&1

fi

echo "중앙신용정보 파일전송 END ..... "  >> ${LOGDIR}/${BATCHNAME}.${DATE}.log 2>&1
