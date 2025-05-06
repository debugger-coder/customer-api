FROM openjdk:17-jdk-slim

# Add metadata labels
LABEL maintainer="example@example.com"
LABEL version="1.0"
LABEL description="Customer API Service"

# Create a non-root user to run the application
RUN addgroup --system --gid 1001 appuser \
    && adduser --system --uid 1001 --gid 1001 appuser

# Set working directory
WORKDIR /app

# Copy the JAR file
COPY build/libs/customer-api.jar app.jar

# Set permissions
RUN chown -R appuser:appuser /app
USER appuser

# Expose the application port
EXPOSE 8080

# Set JVM options
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
