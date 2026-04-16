FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build

COPY pom.xml .
COPY eureka-server/pom.xml eureka-server/
COPY api-gateway/pom.xml api-gateway/
COPY user-service/pom.xml user-service/
COPY property-service/pom.xml property-service/
COPY booking-service/pom.xml booking-service/

RUN mvn dependency:go-offline -pl booking-service -q -B

COPY booking-service/src booking-service/src
RUN mvn package -pl booking-service -DskipTests -q -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /build/booking-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
