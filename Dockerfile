FROM maven:3-jdk-8-slim as build
WORKDIR /build

COPY pom.xml .
COPY src src
COPY .git .git

RUN mvn -B -V clean verify

FROM openjdk:8-jdk-alpine
WORKDIR /bin/yaus

COPY --from=build /build/target/*.jar application.jar
COPY application.properties /etc/yaus/application.properties

ENTRYPOINT ["sh", "-c", "java -Dspring.config.location=file:/etc/yaus/application.properties ${JAVA_OPTS} -jar /bin/yaus/application.jar ${0} ${@}"]