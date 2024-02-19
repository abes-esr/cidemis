LANG=fr_FR.UTF-8
if [[ $(pgrep -cf "cidemis-batch.jar --spring.batch.job.names=exportStatistiques") = 0 ]];
then
    java -Djava.security.egd=file:///dev/urandom -jar cidemis-batch.jar --spring.batch.job.names=exportStatistiques
fi