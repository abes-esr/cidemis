#!/usr/bin/env bash

# dans la cron tab :
# 30 1 1 * * /home/batch/Cidemis/current/bin/cidemisBatchMailCIEPS.sh > /dev/null 2>&1

cd /home/batch/Cidemis/current
LANG=fr_FR.UTF-8
if [[ $(pgrep -cf "CidemisBatch.jar --spring.batch.job.names=mailing") = 0 ]];
then
    echo "launch batch" > /home/batch/Cidemis/logs/cidemis_batch_mail_CIEPS_last_launch.log
    /usr/java/jdk11/bin/java -Djava.security.egd=file:///dev/urandom -jar CidemisBatch.jar --spring.batch.job.names=mailing --server.port=8081 >/dev/null 2>/home/batch/Cidemis/logs/cidemis_batch_Mail_CIEPS_error.log
fi