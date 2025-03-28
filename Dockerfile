FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY . .
RUN chmod +x gradlew & ./gradlew dependencies --no-daemon

RUN ./gradlew assemble --no-daemon

FROM eclipse-temurin:17-jre AS runtime

WORKDIR /app

COPY --from=build /app/build/libs/app-1.0-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]