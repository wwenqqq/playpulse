FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw mvnw
RUN chmod +x mvnw && ./mvnw -q -DskipTests dependency:go-offline
COPY src src
RUN ./mvnw -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN addgroup --system playpulse && adduser --system --ingroup playpulse playpulse
COPY --from=build /workspace/target/playpulse-api-0.0.1-SNAPSHOT.jar app.jar
USER playpulse
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
