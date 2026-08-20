# ClosedWallet Admin Work Guide

## Purpose

Use this guide to perform and verify administrator operations in the ClosedWallet API.

## Start the application

1. Start PostgreSQL.
2. Make sure the `closedwallet` database exists.
3. Start the Spring Boot application:

```powershell
./mvnw.cmd spring-boot:run
```

The API runs at:

```text
http://localhost:8081
```

## Admin account requirement

Admin endpoints require a user whose database role is `ADMIN`.

A normal registration creates a user with role `USER`, so that account cannot access admin endpoints.

Example database update:

```sql
UPDATE users
SET role = 'ADMIN'
WHERE email = 'admin@closedwallet.com';
```

## Login and authorization

Send a login request:

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "email": "admin@closedwallet.com",
  "password": "admin123"
}
```

The response contains the JWT in the `token` property. Save it as the Postman collection variable `authToken` and use it for later requests:

```text
Authorization: Bearer {{authToken}}
```

Always run Login before the first admin request, or after the token expires.

## Admin endpoints

| Operation | Method | Endpoint |
|---|---:|---|
| List users | `GET` | `/api/admin/users` |
| List wallets | `GET` | `/api/admin/wallets` |
| List transactions | `GET` | `/api/admin/transactions` |
| Create merchant | `POST` | `/api/admin/merchants` |
| Freeze wallet | `POST` | `/api/admin/wallets/{id}/freeze` |
| Unfreeze wallet | `POST` | `/api/admin/wallets/{id}/unfreeze` |

### Create merchant payload

```json
{
  "name": "KFC",
  "category": "FOOD",
  "email": "kfc@example.com",
  "phone": "01012345678",
  "logoPath": "/images/kfc.png"
}
```

The current merchant model has no password or merchant login flow, so a `password` field is not stored or used by this endpoint.

### List users with pagination

```http
GET /api/admin/users?page=0&size=20&sort=id,desc
Authorization: Bearer {{authToken}}
```

The response is a Spring `Page` containing fields such as `content` and `totalElements`. The `page`, `size`, and `sort` parameters are applied by the backend.

All of these requests require:

```http
Authorization: Bearer {{authToken}}
```

## Expected security responses

| Situation | Expected status |
|---|---:|
| Valid, non-expired admin token | `200 OK` |
| Missing token | `401 Unauthorized` |
| Invalid or expired token | `401 Unauthorized` |
| Valid token for a `USER` or `MERCHANT` | `403 Forbidden` |
| Nonexistent wallet ID | `404 Not Found` |
| Invalid merchant creation data | `400 Bad Request` |
| Wrong HTTP method | `405 Method Not Allowed` |

## Admin workflow

1. Log in with an account whose role is `ADMIN`.
2. Confirm that the response contains a `token`.
3. Store the token in `authToken`.
4. Run `Get Users` to confirm authorization.
5. Review wallets and transactions.
6. Create merchants when required.
7. Freeze or unfreeze wallets using the wallet ID.
8. Verify the response and check the corresponding audit log.

## Troubleshooting

### `403 Forbidden`

The token may be valid, but the logged-in user does not have the `ADMIN` role. Check the database value:

```sql
SELECT email, role
FROM users
WHERE email = 'admin@closedwallet.com';
```

After changing the role, log in again and replace `authToken` with the newly returned token before calling an admin endpoint. This prevents testing with a stale token. The current JWT implementation derives authorities from the database, but refreshing the token remains the required workflow if role claims are introduced later.

### `401 Unauthorized`

Run Login again and confirm that Postman sends the value as a Bearer token. Do not include the word `Bearer` inside the `authToken` variable itself.

### Wallet action fails

Confirm that the wallet ID exists and that the request uses `POST`, for example:

```http
POST /api/admin/wallets/1/freeze
Authorization: Bearer {{authToken}}
```

### Verify audit activity

After a merchant or wallet action, inspect the newest audit records:

```sql
SELECT *
FROM admin_audit_log
ORDER BY timestamp DESC
LIMIT 10;
```
