if [[ $(pgrep -cf "CidemisBatch.jar --spring.batch.job.names=mailing") = 0 ]];
then
    java -Djava.security.egd=file:///dev/urandom -jar CidemisBatch.jar --spring.batch.job.names=mailing
fi