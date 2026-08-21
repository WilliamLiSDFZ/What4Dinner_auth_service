# AGENTS.md

This file provides guidance to Codex (Codex.ai/code) when working with code in this repository.

## Overview

What4DinnerAuth is the authentication microservice for the What4Dinner platform. It is a Spring Boot 4 (Java 21) app that issues RSA-signed JWTs via two paths: email/password and Google OAuth2. It is also configured as an OAuth2 resource server, so it validates its own JWTs on protected endpoints.

The service runs on port **8081** under context path **`/api`** (so all routes are prefixed, e.g. `/api/v1/email-login`, `/api/health`).

## Commands

```bash
# Run locally (requires application.yaml + RSA keys, see Setup below)
./mvnw spring-boot:run

# Build a jar
./mvnw package

# Run all tests
./mvnw test

# Run a single test class / method
./mvnw test -Dtest=What4DinnerAuthApplicationTests
./mvnw test -Dtest=What4DinnerAuthApplicationTests#methodName

# Docker (wraps `docker compose`)
./deploy.sh           # build + start detached
./deploy.sh logs      # tail app logs
./deploy.sh stop | restart | clean
```

## Required setup before running

The app will not start without these — they are intentionally git-ignored and absent from the repo:

1. **RSA keypair** for JWT signing, at the classpath location referenced in config (`src/main/resources/keys/`):
   ```bash
   mkdir -p src/main/resources/keys
   openssl genrsa -out src/main/resources/keys/private.pem 2048
   openssl rsa -in src/main/resources/keys/private.pem -pubout -out src/main/resources/keys/public.pem
   ```
2. **`application.yaml`**, copied from `application-example.yaml` and filled in with Google OAuth2 client credentials, the PostgreSQL datasource, and the `jwt.private-key` / `jwt.public-key` classpath paths.

The target PostgreSQL database must have a `users` table (see `src/main/resources/database.sql`) with at least: `id` (native `uuid` PK), `family_id` (native `uuid`, NOT NULL, FK to `family(id)`), `email`, `username`, `password_hash` (nullable — null for OAuth-only users), `activated` (boolean), plus a `family` table — every new user gets a freshly created family. The full platform schema (recipes, ingredients, embeddings, etc.) lives in `database.sql` and is applied manually — Spring's `sql.init` only auto-runs `schema.sql`/`data.sql`, so `database.sql` is **not** auto-executed despite `spring.sql.init.mode: always`.

## Architecture

Standard layered Spring structure under `today.what4dinner.what4dinnerauth`: `controller` → `service` (interface + `*Impl`) → `repository` (interface + `*Impl`). DTO is `dto/UserInfo`.

**Two authentication flows produce the same final JWT** (12-hour, claims: `iss=what4dinner-auth`, `sub=userId`, `email`):

- **Email/password** (`AuthController`): `/v1/email-register` (BCrypt-hash, 201 / 409 on duplicate) and `/v1/email-login` (verify, return token / 401). These are the only stateful-credential paths.
- **Google OAuth2** (`OAuthSuccessHandler`): Spring's server-side `oauth2Login` flow. On success the handler upserts the user (`authenticateByGoogle` creates a row with null password if new), mints a **15-minute short-term token**, and redirects the browser to `https://dash.what4dinner.today/callback?code=<token>`. The frontend then calls `GET /v1/exchange-code?code=<token>` to swap the short-term token for a full 12-hour token. This two-token exchange exists because the OAuth callback is a browser redirect and can't return a body — do not "simplify" it into a single token.

  > Note: `API.md` documents an older variant (OAuth returning the token via an `Authorization` header / redirect to `dash.what4dinner.today/`). The code is the source of truth — it uses the `?code=` redirect + `/exchange-code` exchange.

**Family scoping**: every new user gets a brand-new `family` row (`UserInfoServiceImpl` creates it inside the same `@Transactional` method that inserts the user, name `default family name(please change)`). `family_id` is deliberately **not** a JWT claim — family membership is mutable, tokens last 12 hours and cannot be revoked, so a baked-in family id would keep authorizing the family a user just left. Do not "optimize" this back into the token. This service also does not expose a profile/`/me` endpoint: it stays scoped to authentication. Services that need the caller's family read `users.family_id` for the token's `sub` themselves — the whole platform shares one Postgres, so it is a PK lookup.

**JWT signing** (`JwtConfig`): RSA keys are read from PEM `Resource`s at startup and wired into Nimbus `JwtEncoder`/`JwtDecoder` beans. The same public key both signs outgoing tokens and validates incoming ones as a resource server.

**Security** (`SecurityConfig`): stateless sessions, CSRF disabled. Public endpoints are exactly `/health`, `/v1/email-login`, `/v1/email-register`, `/v1/exchange-code`; everything else requires a valid JWT (`oauth2ResourceServer().jwt()`). CORS is locked to origin `https://dash.what4dinner.today` and exposes the `Authorization` header.

## Things to know

- Persistence uses **plain `JdbcTemplate`** (spring-boot-starter-data-jdbc) against PostgreSQL, not JPA. The single repository is `UserRepository`/`UserRepositoryImpl`. The JPA and Redis starters are commented out in `pom.xml`. `RedisRepository`/`RedisRepositoryImpl` and `VerificationCodeController` are empty stubs for planned email-verification work — not wired into anything yet.
- Primary keys are app-generated **UUIDv7** via `util/Uuids.v7()` (time-ordered; `com.fasterxml.uuid:java-uuid-generator`). `insertUser` binds the `java.util.UUID` object directly so PgJDBC maps it to the native `uuid` column — do not bind ids as `String` (Postgres rejects varchar→uuid).
- spring-restdocs is configured (asciidoctor plugin runs at `prepare-package`) but there are no doc snippets yet; the only test is the context-load smoke test.
