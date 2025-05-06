# Customer API

## Overview
A sample RESTful service for managing customer information, including CRUD operations, integration testing, observability, containerization, and CI/CD.

---

## Prerequisites
- Java 17 (the project is configured specifically for Java 17)
- Gradle (wrapper included in the project)
- Docker (for containerization)
- Minikube or Kind (for Kubernetes deployment)

---

## Running the API Locally

```bash
./gradlew bootRun
```

API will be available at: `http://localhost:8080/api/customers`

---

## Testing

Run unit and integration tests:

```bash
./gradlew test
```

Generate test coverage report:

```bash
./gradlew jacocoTestReport
```

Verify test coverage meets minimum threshold (70%):

```bash
./gradlew jacocoTestCoverageVerification
```

---

## API Endpoints

- `POST /api/customers` - Create customer
- `GET /api/customers` - List all customers (supports pagination and sorting)
- `GET /api/customers/{id}` - Get customer by ID
- `PUT /api/customers/{id}` - Update customer
- `DELETE /api/customers/{id}` - Delete customer

### API Documentation

The API is documented using OpenAPI/Swagger. When the application is running, you can access:

- Swagger UI: `http://localhost:8080/swagger-ui`
- OpenAPI JSON: `http://localhost:8080/api-docs`

The documentation includes:
- Detailed endpoint descriptions
- Request/response schemas
- Example values
- Try-it-out functionality to test the API directly from the browser

#### Pagination and Sorting

The GET /api/customers endpoint supports pagination and sorting with the following query parameters:

- `page`: Page number (0-based, default: 0)
- `size`: Page size (default: 10)
- `sort`: Field to sort by (default: customerId)
- `direction`: Sort direction (ASC or DESC, default: ASC)

Example: `/api/customers?page=0&size=5&sort=surname&direction=DESC`

---

## Customer Data Model

| Attribute      | Type   | Constraints       | Field Name     |
|----------------|--------|-------------------|----------------|
| ID             | UUID   | Primary Key       | customerId     |
| First Name     | String | Not Blank         | givenName      |
| Middle Name    | String | Optional          | middleInitial  |
| Last Name      | String | Not Blank         | surname        |
| Email Address  | String | Not Blank, Unique | primaryEmail   |
| Phone Number   | String | Not Blank, Pattern | contactNumber  |

The Customer model uses Lombok annotations to reduce boilerplate code:
- `@Data`: Generates getters, setters, equals, hashCode, and toString methods
- `@Builder`: Implements the Builder pattern for object creation
- `@NoArgsConstructor`: Generates a no-args constructor (required by JPA)
- `@AllArgsConstructor`: Generates a constructor with all fields as parameters

---

## Observability

The application includes comprehensive observability features:

### Health and Metrics
- Spring Boot Actuator endpoints:
  - `/actuator/health` - Health check with detailed status
  - `/actuator/metrics` - Application metrics
  - `/actuator/prometheus` - Prometheus metrics endpoint for scraping

### Structured Logging
- JSON-formatted logs using Logstash encoder
- Mapped Diagnostic Context (MDC) with request information:
  - `requestId` - Unique ID for each request
  - `method` - HTTP method (GET, POST, etc.)
  - `path` - Request path
  - `clientIp` - Client IP address
  - `statusCode` - HTTP status code
  - `duration` - Request processing time in milliseconds

### Distributed Tracing
- Integrated with Micrometer Tracing and Brave
- Compatible with Zipkin for distributed tracing visualization
- Trace context propagation across service calls
- Trace IDs included in log output for correlation

### Monitoring Examples

To view metrics in Prometheus format:
```bash
curl http://localhost:8080/actuator/prometheus
```

To view health status:
```bash
curl http://localhost:8080/actuator/health
```

To set up Zipkin for distributed tracing:
```bash
docker run -d -p 9411:9411 openzipkin/zipkin
```
Then access the Zipkin UI at: http://localhost:9411

---

## Docker

### Single Container

Build and run the application as a Docker container:

```bash
# Build the application
./gradlew bootJar

# Build the Docker image
docker build -t customer-api .

# Run the container
docker run -p 8080:8080 customer-api
```

### Docker Compose

For a complete development environment with monitoring and tracing:

```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

This will start:
- The Customer API on port 8080
- Prometheus for metrics collection on port 9090
- Zipkin for distributed tracing on port 9411

---

## Kubernetes

To deploy to Kubernetes (using Minikube or Kind):

```bash
# Start Minikube (if using Minikube)
minikube start

# Create the namespace
kubectl apply -f k8s/namespace.yaml

# Deploy the application
kubectl apply -f k8s/

# Check deployment status
kubectl get pods -n customer-api
kubectl get services -n customer-api
kubectl get ingress -n customer-api
```

The deployment includes:
- A dedicated namespace for the application
- A Deployment with 2 replicas and resource limits
- A ClusterIP Service exposing port 80 (mapped to container port 8080)
- A ConfigMap for application configuration
- An Ingress for external access
- Health checks and readiness probes
- Prometheus annotations for metrics scraping

---

## CI/CD Pipeline

The project includes a comprehensive CI/CD pipeline implemented with GitHub Actions:

1. **Build Stage**:
   - Code style check with Checkstyle (enforces coding standards)
   - Compile and build the application
   - Run unit and integration tests
   - Generate test coverage report with JaCoCo
   - Verify minimum test coverage (70%)
   - Security vulnerability scanning with Trivy
   - Build Docker image

2. **Development Deployment Stage**:
   - Deploy to development environment
   - Run integration tests against deployed application

3. **Production Deployment Stage**:
   - Manual approval gate
   - Deploy to production environment

The workflow is defined in `.github/workflows/ci.yml`.

---

## CLI Consumer Application

A command-line client application is provided to interact with the API:

```bash
# Build the entire project including the CLI consumer
./gradlew build

# Run the CLI application directly (Unix/Linux/macOS)
./run-cli.sh

# Run the CLI application directly (Windows)
run-cli.bat
```

### Command-line Options

The CLI application supports the following command-line options:

```bash
# Specify a custom API URL
./run-cli.sh -u http://custom-api-url:8080/api/customers

# Enable verbose mode for debugging
./run-cli.sh -v

# Show help information
./run-cli.sh -h
```

### Alternative: Running with Gradle

You can also run the CLI application using Gradle, but it may not work well with interactive input:

```bash
# Run with Gradle
./gradlew :cli-consumer:run

# Run with command-line arguments
./gradlew :cli-consumer:run --args="-v"
```

### Features

The CLI application provides a menu-driven interface to:
- List all customers
- Get customer details by ID
- Create new customers
- Update existing customers
- Delete customers

Additional features:
- Configurable API URL via command-line arguments
- Verbose mode for debugging API requests and responses
- Error handling for common API errors

---

## Database

The application uses an H2 in-memory database by default.

Access the H2 console at: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave empty)

---

## Code Quality

The project includes tools to maintain code quality and consistent style:

### Checkstyle

Checkstyle is configured to enforce coding standards:

```bash
# Run Checkstyle on main source code
./gradlew checkstyleMain

# Run Checkstyle on test source code
./gradlew checkstyleTest
```

The Checkstyle configuration is defined in `config/checkstyle/checkstyle.xml` and enforces:
- Proper import organization (no wildcard imports)
- Code formatting (braces, whitespace, etc.)
- Line length limits (120 characters)
- Common coding practices

### JaCoCo (Java Code Coverage)

JaCoCo is configured to ensure adequate test coverage:

```bash
# Generate coverage report
./gradlew jacocoTestReport

# Verify coverage meets minimum threshold (70%)
./gradlew jacocoTestCoverageVerification
```

---

## Notes

- Email addresses must be unique
- The application validates input data:
  - Required fields (first name, last name, email, phone)
  - Email format validation
  - Phone number format validation (supports formats like 123-456-7890, (123) 456-7890)
  - Unique email constraint
- Structured logging is configured for better observability
- The API follows RESTful principles with appropriate HTTP status codes
- Comprehensive error handling with detailed error responses:
  - 400 Bad Request: For validation errors
  - 404 Not Found: For resources that don't exist
  - 409 Conflict: For data integrity violations (e.g., duplicate email)
  - 500 Internal Server Error: For unexpected errors
