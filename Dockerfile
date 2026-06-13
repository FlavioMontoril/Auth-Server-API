FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY src /app/src
COPY pom.xml /app

RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Cria um usuário e grupo para não rodar como root
RUN addgroup -S spring && adduser -S spring -G spring

# Copia o jar definindo o novo usuário como dono
COPY --from=build /app/target/auth-server-0.0.1-SNAPSHOT.jar /app/app.jar

# Define que o container deve rodar com o usuário criado
USER spring

EXPOSE 8080

CMD [ "java", "-jar", "app.jar" ]
