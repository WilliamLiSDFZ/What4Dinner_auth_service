# What4DinnerAuth - Frontend Integration Guide

Base URL: `https://auth.what4dinner.today/api` (local: `http://localhost:8081/api`)

## Authentication

All protected endpoints require a JWT token in the `Authorization` header:

```
Authorization: Bearer <token>
```

The JWT is valid for 60 minutes and contains:
- `sub` — user ID
- `email` — user email
- `iss` — `what4dinner-auth`

---

## Endpoints

### 1. Register (Email)

```
POST /v1/email-register
Content-Type: application/x-www-form-urlencoded
```

**Parameters:**

| Name     | Type   | Required | Description       |
|----------|--------|----------|-------------------|
| email    | string | yes      | User email        |
| username | string | yes      | Display name      |
| password | string | yes      | Account password  |

**Success (201):**
```json
{
  "email": "user@example.com",
  "username": "John"
}
```

**Error — email already exists (409):**
```json
{
  "error": "Email already registered"
}
```

**Example:**
```javascript
const res = await fetch('/api/v1/email-register', {
  method: 'POST',
  headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
  body: new URLSearchParams({ email, username, password })
});
```

---

### 2. Login (Email)

```
POST /v1/email-login
Content-Type: application/x-www-form-urlencoded
```

**Parameters:**

| Name     | Type   | Required | Description      |
|----------|--------|----------|------------------|
| email    | string | yes      | User email       |
| password | string | yes      | Account password |

**Success (200):**
```json
{
  "token": "eyJhbGciOiJSUzI1NiJ9...",
  "token_type": "Bearer",
  "email": "user@example.com"
}
```

**Error — invalid credentials (401):**
```json
{
  "error": "Invalid email or password"
}
```

**Example:**
```javascript
const res = await fetch('/api/v1/email-login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
  body: new URLSearchParams({ email, password })
});
const data = await res.json();
// Store data.token for subsequent requests
localStorage.setItem('token', data.token);
```

---

### 3. Google OAuth2 Login

This uses Spring's server-side OAuth2 flow. The frontend does **not** call an API — it navigates the browser directly.

**Step 1 — Redirect the user to:**

```
GET /api/oauth2/authorization/google
```

This redirects to Google's consent screen. After the user authorizes, Google calls back to the server.

**Step 2 — After successful authentication:**

The server redirects the browser to `https://dash.what4dinner.today/` with the JWT in the `Authorization` response header.

> **Note:** Since this is an HTTP redirect (302), the `Authorization` header may not be accessible to frontend JavaScript. If you need to capture the token, coordinate with the backend to pass it as a URL query parameter instead (e.g., `?token=...`).

**Example (triggering the flow):**
```javascript
// Simply navigate — this is a full-page redirect, not an AJAX call
window.location.href = '/api/oauth2/authorization/google';
```

---

### 4. Health Check

```
GET /health
```

**Response (200):**
```json
{
  "status": "UP"
}
```

---

## Using the JWT Token

After login, include the token in all requests to protected endpoints:

```javascript
const res = await fetch('/api/v1/some-protected-endpoint', {
  headers: {
    'Authorization': `Bearer ${localStorage.getItem('token')}`
  }
});
```

**Token expiration:** If you receive a `401` response on a protected endpoint, the token has expired. Redirect the user to log in again.