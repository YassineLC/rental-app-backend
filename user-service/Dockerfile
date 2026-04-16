FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build

COPY pom.xml .
COPY eureka-server/pom.xml eureka-server/
COPY api-gateway/pom.xml api-gateway/
COPY user-service/pom.xml user-service/
COPY property-service/pom.xml property-service/
COPY booking-service/pom.xml booking-service/

RUN mvn dependency:go-offline -pl user-service -q -B

COPY user-service/src user-service/src
RUN mvn package -pl user-service -DskipTests -q -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /build/user-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
