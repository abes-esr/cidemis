if [[ $(pgrep -cf "cidemis-batch.jar --spring.batch.job.names=mailing") = 0 ]];
then
    java -Djava.security.egd=file:///dev/urandom -jar cidemis-batch.jar --spring.batch.job.names=mailing
fi