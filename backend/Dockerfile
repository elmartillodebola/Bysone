# ── Etapa 1: build ────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copiar wrapper y pom primero (cache de capas Maven)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B

# Copiar fuentes y compilar
COPY src ./src
RUN ./mvnw package -DskipTests -B

# ── Etapa 2: runtime ──────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Usuario sin privilegios
RUN addgroup -S bysone && adduser -S bysone -G bysone
USER bysone

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
