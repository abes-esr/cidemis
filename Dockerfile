###
# Image pour la compilation
FROM maven:3-eclipse-temurin-11 as build-image
WORKDIR /build/
# Installation et configuration de la locale FR
RUN apt update && DEBIAN_FRONTEND=noninteractive apt -y install locales
RUN sed -i '/fr_FR.UTF-8/s/^# //g' /etc/locale.gen && \
    locale-gen
ENV LANG fr_FR.UTF-8
ENV LANGUAGE fr_FR:fr
ENV LC_ALL fr_FR.UTF-8


# On lance la compilation Java
# On débute par une mise en cache docker des dépendances Java
# cf https://www.baeldung.com/ops/docker-cache-maven-dependencies
COPY ./pom.xml /build/pom.xml
COPY ./core/pom.xml /build/core/pom.xml
COPY ./web/pom.xml /build/web/pom.xml
COPY ./batch/pom.xml /build/batch/pom.xml
RUN mvn verify --fail-never
# et la compilation du code Java
COPY ./core/   /build/core/
COPY ./web/    /build/web/
COPY ./batch/  /build/batch/
RUN mvn --batch-mode \
        -Dmaven.test.skip=false \
        -Duser.timezone=Europe/Paris \
        -Duser.language=fr \
        package

FROM 9.0.59-jdk11-temurin as web-image
COPY --from=build-image /build/web/target/*.war /usr/local/tomcat/webapps/
ENV TZ=Europe/Paris
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
CMD ["catalina.sh", "run"]


