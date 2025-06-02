# cidemis
Circuit Dématérialisé des Demandes ISSN

Configuration locale minimale :
- mettre une JDK11 (ctrl alt shift s sur intellij, projet structure)
- créer un fichier application-localhost.properties placer dans le répertoire ressources du module web
- renseigner les couples clé / valeur ci dessous

Note: ces valeurs sont susceptibles d'évoluer (observer les logs d'erreur de lancement de l'appli pour des valeurs ou clés manquantes)
```txt
cidemis.datasource.url=
cidemis.datasource.username=
cidemis.datasource.password=
cidemis.datasource.driver-class-name=oracle.jdbc.OracleDriver
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.Oracle12cDialect
spring.batch.jdbc.initialize-schema=never
logging.config=classpath:log4j2-all.xml
psi.lien.perenne=
code.pays.url=
psi.lien.perenne.issn=
cbs.url=
cbs.port=
cbs.login=
cbs.password=
date.borneInf=
date.borneSup=
path.statistiques=
path.mail.mensuel=
mail.ws.url=
mail.ws.skip=
cidemis.url=
logging.level.fr.abes=
logging.level.root=
spring.jpa.open-in-view=
spring.jpa.database-platform=org.hibernate.dialect.Oracle12cDialect
path.justificatifs=
wsAuthSudoc.url=
```

Pour les variables mentionnant des /applis/dossier1/etc..
Penser à créer sur son disque c, à la racine, un dossier applis, et les sous dossiers qui peuvent etre mentionnés dans les couple clé valeurs de propriétés de application-localhost.properties

Aller chercher les valeurs dans les fichiers d'environnement dans le repertoire docker du projet, situé sur les serveurs de l'abes. Pour un externe : demander les chaines de connexion à
soa@abes.fr

- Télécharger un Tomcat 9 sur https://tomcat.apache.org/download-90.cgi en zip, et dezipper
- L'installer et mettre la configuration d'execution suivante dans intellij.
- A côté du run classique, faire edit configurations, créer avec + en haut a gauche une nouvelle configuration tomcat
- Faire pointer dans l'espace Application -> vers le dossier tomcat préalablement dézippé 
- Mettre en url : http://localhost:8080/web_war_exploded/
- Mettre en JRE : coretto-11 (ou une autre jdk 11)
- HTTP port : 8080
- JMX port : 1099
- Dans before launch, faire +, puis Build Artifacts, et selection web: war exploded

Sauvegarder et lancer le projet (pensez à lancer son VPN pour éviter des erreurs 403 d'accès sur des services)