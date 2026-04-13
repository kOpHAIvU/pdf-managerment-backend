# PDF Desk Backend

GraphQL backend for PDF Desk, built with Spring Boot.  
It provides authentication, document management, permission control, sharing, and audit logging.

## Tech Stack

- Java 17
- Spring Boot 4
- Spring GraphQL
- Spring Security + JWT
- AWS DynamoDB (SDK v2 Enhanced Client)
- Redis (cache/rate-limit support)
- Kafka (event messaging support)

## Features

- User authentication:
  - register and login with email/password
  - login with Google ID token
- JWT-protected GraphQL operations
- Document management:
  - create documents
  - fetch a single document
  - list documents with cursor-based pagination
- Sharing and access control:
  - share a document with another user and assign role (`VIEWER`, `COMMENTER`, `EDITOR`, `OWNER`)
  - create share links with optional expiration
  - visibility levels (`PRIVATE`, `RESTRICTED`, `PUBLIC`)
- Audit logging for important document actions
- Rate limiting filter for request protection

## Prerequisites

- JDK 17+
- Maven 3.9+ (or use the included Maven Wrapper)
- Running services depending on your environment:
  - DynamoDB
  - Redis
  - Kafka

## Installation

```bash
git clone https://github.com/<your-org-or-user>/pdf-managerment-backend.git
cd pdf-managerment-backend
```

If you use Maven Wrapper:

```bash
./mvnw clean install
```

On Windows PowerShell:

```powershell
.\mvnw.cmd clean install
```

## Configuration

The app reads configuration from `src/main/resources/application.yml` and environment variables.

Common variables:

- `GOOGLE_CLIENT_ID`
- `CORS_ALLOWED_ORIGINS` (comma-separated)
- `REDIS_HOST`
- `REDIS_PORT`
- `KAFKA_BOOTSTRAP_SERVERS`
- `RATE_LIMIT_CAPACITY`
- `RATE_LIMIT_REFILL_PER_MINUTE`

## Run the Project

Using Maven Wrapper:

```bash
./mvnw spring-boot:run
```

Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Default server URL: `http://localhost:8080`  
GraphQL endpoint: `http://localhost:8080/graphql`  
GraphiQL: `http://localhost:8080/graphiql`

## Using the API (GraphQL)

Example `register` mutation:

```graphql
mutation {
  register(fullName: "Alice Doe", email: "alice@example.com", password: "strong-password") {
    token
    user {
      id
      email
      fullName
    }
  }
}
```

Example `createDocument` mutation:

```graphql
mutation {
  createDocument(title: "Project Spec", visibility: PRIVATE) {
    id
    title
    visibility
  }
}
```

Example `documents` query:

```graphql
query {
  documents(first: 20, after: null) {
    edges {
      cursor
      node {
        id
        title
        visibility
      }
    }
    pageInfo {
      endCursor
      hasNextPage
    }
  }
}
```

## Repository Structure

- `src/main/java/com/pdfdesk/service/auth` - authentication logic and GraphQL auth resolver
- `src/main/java/com/pdfdesk/service/document` - document domain models, resolvers, and services
- `src/main/java/com/pdfdesk/service/permission` - document permission model and access checks
- `src/main/java/com/pdfdesk/service/sharing` - user/link sharing logic
- `src/main/java/com/pdfdesk/service/users` - user model, repository, and service
- `src/main/java/com/pdfdesk/service/config` - security and infrastructure configuration
- `src/main/resources/graphql` - GraphQL schema
- `src/main/resources/application.yml` - application configuration
