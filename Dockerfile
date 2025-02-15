# ===========================
# 1️⃣ Build Stage (Compiles the JAR)
# ===========================
FROM docker.io/library/eclipse-temurin:21-jdk-alpine AS builder

# Set working directory
WORKDIR /src/eshop

# Copy project files into container
COPY . .

# Build the JAR file using Gradle
RUN ./gradlew clean bootJar

# ===========================
# 2️⃣ Run Stage (Creates a lightweight container)
# ===========================
FROM docker.io/library/eclipse-temurin:21-jre-alpine AS runner

# Set up a non-root user
ARG USER_NAME=eshop
ARG USER_UID=1000
ARG USER_GID=${USER_UID}

RUN addgroup -g ${USER_GID} ${USER_NAME} \
    && adduser -h /opt/eshop -D -u ${USER_UID} -G ${USER_NAME} ${USER_NAME}

# Switch to non-root user
USER ${USER_NAME}

# Set the working directory inside the container
WORKDIR /opt/eshop

# Copy the JAR file from the builder stage
COPY --from=builder --chown=${USER_UID}:${USER_GID} /src/eshop/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java"]
CMD ["-jar", "app.jar"]
