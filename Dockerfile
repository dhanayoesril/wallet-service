FROM eclipse-temurin:11-jdk AS build
WORKDIR /app

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew

COPY src src

RUN ./gradlew --no-daemon -Dorg.gradle.java.installations.auto-download=true shadowJar

FROM eclipse-temurin:11-jre
WORKDIR /app

COPY --from=build /app/build/libs/*-all.jar /app/app.jar

ENV DB_HOST=db DB_PORT=5432 DB_NAME=wallet_db DB_USER=user DB_PASSWORD=secret

CMD ["java", "-jar", "/app/app.jar"]

EXPOSE 8080

