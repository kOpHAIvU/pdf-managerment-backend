# Notiq Backend

Backend service for **Notiq**, a Notion-inspired workspace app for creating, organizing, and sharing knowledge.

## 🔗 Related Repository 
👉 Frontend App: https://github.com/kOpHAIvU/notiq-viewer

## What this service does

- Exposes a **GraphQL API** for user/workspace/content operations
- Handles authentication and authorization
- Integrates with cloud services for file storage and payment flows
- Provides background/event-ready architecture with Redis + Kafka

## Core features

- **Google OAuth login** for quick sign-in
- **Document and media storage** with Amazon **S3**
- **NoSQL data model** powered by **DynamoDB**
- **Online payment integrations** with **ZaloPay** and **VNPay**
- **Rate limiting and caching** with Redis

## Tech stack

- **Java**: 17
- **Framework**: Spring Boot (Maven)
- **API**: GraphQL (`/graphql`)
- **Data**: DynamoDB
- **Storage**: Amazon S3
- **Infra (local/dev)**: Redis, Kafka, LocalStack

## Run with Docker (recommended)

Start app and dependencies:

```bash
docker compose up --build
```

Service endpoints:

- GraphQL: `http://localhost:8080/graphql`
- Health check: `http://localhost:8080/actuator/health`

## Local development

Start dependencies only:

```bash
docker compose up redis kafka localstack
```

Run Spring Boot locally:

```bash
./mvnw spring-boot:run
```

## Configuration

Main config file: `src/main/resources/application.yml`

Common environment variables:

- `GOOGLE_CLIENT_ID`: Google OAuth client ID
- `CORS_ALLOWED_ORIGINS`: default `http://localhost:3000`
- `REDIS_HOST`: default `localhost`
- `REDIS_PORT`: default `6379`
- `KAFKA_BOOTSTRAP_SERVERS`: default `localhost:9092`
- `RATE_LIMIT_CAPACITY`: default `120`
- `RATE_LIMIT_REFILL_PER_MINUTE`: default `120`

You can provide env values to Docker via `./.env`:

```bash
GOOGLE_CLIENT_ID=your-google-client-id
CORS_ALLOWED_ORIGINS=http://localhost:3000
```

## Useful commands

```bash
./mvnw test
./mvnw -DskipTests package
```

## Notes

- LocalStack emulates DynamoDB for local testing.
- Table bootstrap logic is in `localstack/init-dynamodb.sh`.
- Default backend port is `8080`.