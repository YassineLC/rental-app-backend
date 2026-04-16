FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build

COPY pom.xml .
COPY eureka-server/pom.xml eureka-server/
COPY api-gateway/pom.xml api-gateway/
COPY user-service/pom.xml user-service/
COPY property-service/pom.xml property-service/
COPY booking-service/pom.xml booking-service/

RUN mvn dependency:go-offline -pl api-gateway -q -B

COPY api-gateway/src api-gateway/src
RUN mvn package -pl api-gateway -DskipTests -q -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /build/api-gateway/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
