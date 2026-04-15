# PDF Desk Backend

Production-oriented backend for PDF management with JWT security, layered architecture, validated APIs, and DynamoDB persistence.

## Features

- Layered module design for PDF domain:
  - `controller`
  - `service`
  - `repository`
  - `dto`
  - `entity`
- Secure PDF upload:
  - PDF-only validation
  - size limit enforcement
  - unique file naming
  - local storage simulation for cloud object storage
- JWT authentication middleware and protected PDF routes
- Standardized API response format:
  - `success`
  - `message`
  - `data`
- Global REST exception handling and validation error responses
- Pagination and filename search for PDF listing
- Update metadata and delete PDF endpoints
- Structured request and error logging
- Unit and integration test coverage for PDF module
- OpenAPI specification and Docker support

## Architecture

The codebase is organized by bounded features, and each feature follows clean layering.

Example PDF module:

- `src/main/java/com/pdfdesk/service/pdf/controller` - API endpoint layer
- `src/main/java/com/pdfdesk/service/pdf/service` - business logic
- `src/main/java/com/pdfdesk/service/pdf/repository` - persistence access
- `src/main/java/com/pdfdesk/service/pdf/dto` - API contracts
- `src/main/java/com/pdfdesk/service/pdf/entity` - DynamoDB entity model

Cross-cutting modules:

- `src/main/java/com/pdfdesk/service/security` - JWT and auth filters
- `src/main/java/com/pdfdesk/service/config` - security config, error handling, request logging
- `src/main/java/com/pdfdesk/service/common` - shared exceptions and API response wrappers

## Tech Stack

- Java 17
- Spring Boot 4
- Spring Web + Spring GraphQL
- Spring Security + JWT
- DynamoDB (AWS SDK v2 enhanced client)
- Redis, Kafka (existing integrations)
- OpenAPI (springdoc + static spec in `docs/openapi.yaml`)

## Setup

### Prerequisites

- Java 17+
- Maven 3.9+
- DynamoDB, Redis, and Kafka available for full environment

### Installation

```bash
git clone https://github.com/<your-org-or-user>/pdf-managerment-backend.git
cd pdf-managerment-backend
```

```bash
./mvnw clean install
```

On Windows PowerShell:

```powershell
.\mvnw.cmd clean install
```

### Environment Configuration

Use `.env` values (already included in project root) or export equivalent variables:

- `JWT_SECRET`
- `JWT_EXPIRATION_MS`
- `MAX_UPLOAD_FILE_SIZE`
- `MAX_UPLOAD_REQUEST_SIZE`
- `LOCAL_UPLOAD_DIR`
- `CORS_ALLOWED_ORIGINS`
- `REDIS_HOST`
- `REDIS_PORT`
- `KAFKA_BOOTSTRAP_SERVERS`

## Run

```bash
./mvnw spring-boot:run
```

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Base URL: `http://localhost:8080`

## API Documentation

- OpenAPI YAML: `docs/openapi.yaml`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## REST Endpoints (PDF Module)

- `POST /api/pdfs` - upload PDF
- `GET /api/pdfs?page=0&size=10&search=term` - list with pagination and search
- `PATCH /api/pdfs/{pdfId}` - update filename metadata
- `DELETE /api/pdfs/{pdfId}` - delete PDF

All endpoints are JWT-protected and require:

`Authorization: Bearer <token>`

## Testing

Run all tests:

```bash
./mvnw test
```

Windows:

```powershell
.\mvnw.cmd test
```

Included tests:

- Unit: `PdfServiceImplTest`
- Integration: `PdfControllerIntegrationTest`

## Docker

Build image:

```bash
docker build -t pdf-desk-backend .
```

Run container:

```bash
docker run -p 8080:8080 --env-file .env pdf-desk-backend
```
