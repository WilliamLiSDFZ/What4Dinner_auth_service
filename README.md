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
| `POST /api/v1/email-login` | No | Login, returns a 60-minute JWT |
| `GET /api/oauth2/authorization/google` | No | Start Google OAuth2 flow |
| `GET /api/v1/exchange-code` | No | Exchange the OAuth `code` for a 60-minute JWT |
| `GET /api/health` | No | Health check |

### Google OAuth2 handoff

Because OAuth success is a browser redirect (no response body), the token is delivered in two steps:

1. Send the user to `GET /api/oauth2/authorization/google`.
2. After Google login, the server upserts the user and redirects the browser to
   `https://dash.what4dinner.today/callback?code=<token>`, where `code` is a short-lived (15-minute) JWT.
3. The frontend reads `code` from the URL and calls `GET /api/v1/exchange-code?code=<token>`, which
   returns the usable 60-minute JWT in the JSON body (`{ "token": "..." }`) for use as `Authorization: Bearer`.
