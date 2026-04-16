FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build

COPY pom.xml .
COPY eureka-server/pom.xml eureka-server/
COPY api-gateway/pom.xml api-gateway/
COPY user-service/pom.xml user-service/
COPY property-service/pom.xml property-service/
COPY booking-service/pom.xml booking-service/

RUN mvn dependency:go-offline -pl eureka-server -q -B

COPY eureka-server/src eureka-server/src
RUN mvn package -pl eureka-server -DskipTests -q -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /build/eureka-server/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
