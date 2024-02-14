#!/usr/bin/env bash

# dans la cron tab :
# 30 1 1 * * /home/batch/Cidemis/current/bin/cidemisBatchExportStatistiques.sh > /dev/null 2>&1

cd /home/batch/Cidemis/current
LANG=fr_FR.UTF-8
if [[ $(pgrep -cf "CidemisBatch.jar --spring.batch.job.names=exportStatistiques") = 0 ]];
then
    echo "launch batch" > /home/batch/Cidemis/logs/cidemis_batch_export_statistiques_last_launch.log
    /usr/java/jdk11/bin/java -Djava.security.egd=file:///dev/urandom -jar CidemisBatch.jar --spring.batch.job.names=exportStatistiques --server.port=8081 >/dev/null 2>/home/batch/Cidemis/logs/cidemis_batch_export_statistiques_error.log
fi