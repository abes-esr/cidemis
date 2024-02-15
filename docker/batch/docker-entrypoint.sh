#!/bin/bash

# Paramètres par défaut du conteneur
export CIDEMIS_BATCH_CRON_STATS=${CIDEMIS_BATCH_CRON_STATS:='50 8 1 * *'}
export CIDEMIS_BATCH_CRON_MAILING=${CIDEMIS_BATCH_CRON_MAILING:='50 8 1 * *'}
export CIDEMIS_BATCH_LAUNCH=${CIDEMIS_BATCH_LAUNCH:=0}

# Réglage de /etc/environment pour que les crontab s'exécutent avec les bonnes variables d'env
echo "$(env)
LANG=fr_FR.UTF-8" > /etc/environment

if [ CIDEMIS_BATCH_LAUNCH -eq 1 ];
then
  # Charge la crontab depuis le template
  envsubst < /etc/cron.d/tasks.tmpl > /etc/cron.d/tasks
  echo "-> Installation des crontab :"
  cat /etc/cron.d/tasks
  crontab /etc/cron.d/tasks

  # execute CMD (crond)
  exec "$@"
fi