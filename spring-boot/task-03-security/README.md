# Task 3 - CRUD application: security

The CRUD application from Task 2 secured with OAuth2 and JWT.

- **Authorization Server** (Spring Authorization Server, embedded): authorization code flow with PKCE,
  public client `crud-client`, issues signed JWT access tokens with a `roles` claim.
- **Resource Server**: `/api/**` is stateless and validates the JWT on every request.
- **Roles**: `USER` can read (`GET`), `ADMIN` can also create, update and delete.

| User | Password | Role |
|---|---|---|
| `user` | `user` | `USER` |
| `admin` | `admin` | `ADMIN` |

Demo credentials are hard-coded in `SecurityConfig` for the homework only.

## Run

```
mvn spring-boot:run
```

## Get a token

1. Generate a PKCE pair:
   ```
   VERIFIER=$(openssl rand -base64 48 | tr -d '=+/\n' | cut -c1-64)
   CHALLENGE=$(printf %s "$VERIFIER" | openssl dgst -sha256 -binary | openssl base64 -A | tr '+/' '-_' | tr -d '=')
   ```
2. Open in a browser and log in as `user` or `admin`:
   ```
   http://localhost:8080/oauth2/authorize?response_type=code&client_id=crud-client&scope=openid&redirect_uri=http://127.0.0.1:8080/authorized&code_challenge=<CHALLENGE>&code_challenge_method=S256
   ```
3. The browser is redirected to `http://127.0.0.1:8080/authorized?code=<CODE>` (the page itself is a 404, only the URL matters).
4. Exchange the code for a token:
   ```
   curl -X POST http://localhost:8080/oauth2/token \
     -d "grant_type=authorization_code&client_id=crud-client&code=<CODE>&redirect_uri=http://127.0.0.1:8080/authorized&code_verifier=$VERIFIER"
   ```

## Call the API

```
curl -H "Authorization: Bearer <access_token>" http://localhost:8080/api/products
```

| Request | No token | `USER` | `ADMIN` |
|---|---|---|---|
| `GET /api/products` | 401 | 200 | 200 |
| `POST`, `PUT`, `DELETE /api/products` | 401 | 403 | 2xx |
