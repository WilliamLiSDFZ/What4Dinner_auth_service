# What4DinnerAuth

Authentication microservice for the [What4Dinner](https://what4dinner.today) platform.

## Tech Stack

- Java 21 / Spring Boot 4.0.3
- Spring Security (JWT + Google OAuth2)
- PostgreSQL (JDBC)
- Docker

## Getting Started

### Prerequisites

- Java 21
- PostgreSQL database
- Google OAuth2 credentials (from [Google Cloud Console](https://console.cloud.google.com/apis/credentials))

### Setup

1. **Generate RSA keys** for JWT signing:

```bash
mkdir -p src/main/resources/keys
openssl genrsa -out src/main/resources/keys/private.pem 2048
openssl rsa -in src/main/resources/keys/private.pem -pubout -out src/main/resources/keys/public.pem
```

2. **Create application config** from the example:

```bash
cp src/main/resources/application-example.yaml src/main/resources/application.yaml
```

Then fill in your Google OAuth2 credentials and PostgreSQL connection details.

3. **Run**:

```bash
./mvnw spring-boot:run
```

The service starts at `http://localhost:8081/api`.

### Docker

```bash
# Build and start
./deploy.sh

# Or use docker compose directly
docker compose up --build

# Other commands
./deploy.sh stop      # stop
./deploy.sh logs      # tail logs
./deploy.sh restart   # restart
./deploy.sh clean     # remove containers and images
```

Set environment variables before running:

| Variable | Description |
|---|---|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Database password |
| `GOOGLE_CLIENT_ID` | Google OAuth2 client ID |
| `GOOGLE_CLIENT_SECRET` | Google OAuth2 client secret |

## API

See [API.md](API.md) for the full frontend integration guide.

| Endpoint | Auth | Description |
|---|---|---|
| `POST /api/v1/email-register` | No | Register with email/password |
| `POST /api/v1/email-login` | No | Login, returns JWT |
| `GET /api/oauth2/authorization/google` | No | Start Google OAuth2 flow |
| `GET /api/health` | No | Health check |
