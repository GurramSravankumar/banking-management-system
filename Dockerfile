# Stage 1: Build Java Project
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Cache Maven dependencies by copying pom.xml first
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source tree and compile jar
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime Environment
FROM eclipse-temurin:21-jre
WORKDIR /app

# Install native Tesseract OCR binaries for Tess4J wrapper support
RUN apt-get update && apt-get install -y \
    tesseract-ocr \
    tesseract-ocr-eng \
    && rm -rf /var/lib/apt/lists/*

# Copy built package from build stage
COPY --from=build /app/target/SpringSecurity-0.0.1-SNAPSHOT.jar app.jar

# Create user uploads folder matching local backend environment
RUN mkdir -p uploads && chmod 777 uploads

# Default Database connection environment variables profiles
ENV SPRING_DATASOURCE_URL=jdbc:mysql://mysql-db:3306/bankdb?createDatabaseIfNotExist=true \
    SPRING_DATASOURCE_USERNAME=root \
    SPRING_DATASOURCE_PASSWORD=root \
    SERVER_PORT=8080

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
