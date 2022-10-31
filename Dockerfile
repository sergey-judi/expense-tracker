FROM gradle:7.3.3-jdk11 AS builder
WORKDIR /workdir/server
COPY . /workdir/server
RUN gradle build -x test

FROM openjdk:11-jre-slim
EXPOSE 8181
COPY --from=builder /workdir/server/build/libs/*.jar /app/spring-boot-server.jar
ENTRYPOINT ["java", "-jar", "/app/spring-boot-server.jar"]
