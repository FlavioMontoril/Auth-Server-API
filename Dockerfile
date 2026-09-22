FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY src /app/src
COPY pom.xml /app

RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Cria um usuário e grupo para não rodar como root
# RUN addgroup -S spring && adduser -S spring -G spring && chown spring:spring /app
# 1. Cria o usuário/grupo spring e a pasta de uploads com as permissões corretas
RUN addgroup -S spring && adduser -S spring -G spring \
    && mkdir -p /var/lib/auth-server/uploads \
    && chown -R spring:spring /app /var/lib/auth-server

# Copia o jar definindo o novo usuário como dono
COPY --chown=spring:spring --from=build /app/target/auth-server-0.0.2.jar /app/app.jar

# Define que o container deve rodar com o usuário criado
USER spring

EXPOSE 8080

CMD [ "java", "-jar", "app.jar" ]
