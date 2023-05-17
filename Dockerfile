
FROM eclipse-temurin:11-jdk-alpine as build
WORKDIR /workspace/app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw
COPY src src

RUN ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM eclipse-temurin:11-jdk-alpine

VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

ENV URL_KEYCLOAK https://mihouodie.com
ENV REALM_KEYCLOAK skitter
ENV RESOURCE_KEYCLOAK backend
ENV MASTER_ACCESS_KEY arquitectura
ENV MASTER_SECRET_KEY software
ENV SECRET_CREDENTIAL_KEYCLOAK Xs4SzmEkj3mmoGmv8Zf2ovk4fIe2wzVx
ENV ZIPKIN_URL http://localhost:9910
ENV DB_PASSWORD mypass
ENV DB_USERNAME postgres
ENV DB_URL jdbc:postgresql://localhost:5555/currency
ENV LOGS_URI 67.205.142.137:5000
ENV CONFIG_SERVER_URL http://68.183.104.65:9090
ENV SERVER_PROFILE test
ENTRYPOINT ["java", "-cp","app:app/lib/*", "com.ucb.bo.sktmsuser.SktMsUserApplicationKt"]


