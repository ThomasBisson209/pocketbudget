# ─────────────────────────────────────────────────────────────────────────────
# Stage 1 — Build the React frontend
# ─────────────────────────────────────────────────────────────────────────────
FROM node:20-alpine AS frontend
WORKDIR /app/frontend

# Install deps first (cached layer)
COPY frontend/package*.json ./
RUN npm ci

# Build
COPY frontend/ ./
RUN npm run build


# ─────────────────────────────────────────────────────────────────────────────
# Stage 2 — Build the Java backend (fat JAR) with frontend bundled inside
# ─────────────────────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17-alpine AS backend
WORKDIR /app

# Download deps first (cached layer)
COPY backend/pom.xml .
RUN mvn dependency:go-offline -q

# Copy source + inject frontend dist into classpath resources
COPY backend/src ./src
COPY --from=frontend /app/frontend/dist ./src/main/resources/static

# Package (skip tests — CI runs them separately)
RUN mvn package -DskipTests -q


# ─────────────────────────────────────────────────────────────────────────────
# Stage 3 — Minimal runtime image
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the fat JAR produced by maven-shade-plugin
COPY --from=backend /app/target/pocketbudget.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
