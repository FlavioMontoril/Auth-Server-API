FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY src /app/src
COPY pom.xml /app

RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/target/auth-server-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

CMD [ "java", "-jar", "app.jar" ]
