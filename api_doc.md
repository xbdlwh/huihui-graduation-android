# Huihui Server API

## Base
- Base URL: `http://<host>:<port>`
- Content-Type: `application/json`

## Endpoints

### GET /
Returns a simple string.

Request
- Method: `GET`
- Path: `/`
- Body: none

Response
- Status: `200`
- Body: `"hello"`

---

### POST /auth/login
Login with username and password.

Request
- Method: `POST`
- Path: `/auth/login`
- Body:
```json
{
  "username": "string",
  "password": "string"
}
```

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": {
    "token": "<jwt>"
  }
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "SqlError(...) or JwtError(...)"
}
```

---

### POST /auth/register
Register a new user.

Request
- Method: `POST`
- Path: `/auth/register`
- Body:
```json
{
  "email": "user@example.com",
  "username": "string",
  "password": "string"
}
```

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": {
    "token": "<jwt>"
  }
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "SqlError(...) or JwtError(...)"
}
```

---

### GET /auth/me
Fetch current user using an auth token.

Request
- Method: `GET`
- Path: `/auth/me`
- Headers:
  - `Authorization: Bearer <jwt>`
- Body: none

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "name": "username"
  }
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "SqlError(...) or JwtError(...)"
}
```

## Notes
- The API wraps all responses in `ApiResponse`.
- Errors are returned with `code=500` inside the JSON body (HTTP status remains 200).
- `name` in `CurrentUser` is mapped from the stored `username`.
- JWTs are signed with `JWT_SECRET` (defaults to `dev-secret-change-me` if unset) and expire in 24 hours.
