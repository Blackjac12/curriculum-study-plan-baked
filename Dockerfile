FROM gradle:8.4-jdk17 AS builder
WORKDIR /home/gradle/project

COPY --chown=gradle:gradle . .
RUN chmod +x ./gradlew && ./gradlew clean build -x test --no-daemon


FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /home/gradle/project/build/libs/*.jar /app/app.jar

EXPOSE 8080

ENV JAVA_OPTS=""
CMD ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
